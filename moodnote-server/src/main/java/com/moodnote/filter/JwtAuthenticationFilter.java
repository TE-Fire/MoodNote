package com.moodnote.filter;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.moodnote.common.constant.MessageConstant;
import com.moodnote.common.constant.RedisKeyConstant;
import com.moodnote.common.exception.ErrorCode;
import com.moodnote.common.utils.JwtTokenUtil;
import com.moodnote.common.utils.RedisUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@WebFilter(urlPatterns = "/api/*")
public class JwtAuthenticationFilter implements Filter{
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisUtil redisUtil;

    private static final Set<String> EXCLUDE_PATH = Set.of(
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/captcha",
        "/api/auth/send-code",
        "/api/auth/reset-password"
    );


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        log.info("doFilter处理开始");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();
        String method = request.getMethod();
        log.info("拦截uri: {}, method: {}", uri, method);
        
        // 1. 白名单放行
        if (EXCLUDE_PATH.contains(uri)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 2. OPTIONS 请求处理
        if ("OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        // 3. 获取token
        String token = extractToken(request);
        
        // 4. 检查token是否为空
        if (token == null) {
            sendUnauthorizedResponse(response, MessageConstant.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
            return;
        }

        // 5. 验证token有效性
        try {
            if (!jwtTokenUtil.validateToken(token)) {
                sendUnauthorizedResponse(response, MessageConstant.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
                return;
            }

            // 6. 检查黑名单
            String blackKey = RedisKeyConstant.getBlacklistKey(token);
            if (Boolean.TRUE.equals(redisUtil.hasKey(blackKey))) {
                sendUnauthorizedResponse(response, MessageConstant.TOKEN_BLACKLIST, ErrorCode.TOKEN_BLACKLIST);
                return;
            }

            // 7. 将用户信息存入请求属性中
            String userName = jwtTokenUtil.extractUsername(token);
            Long userId = jwtTokenUtil.extractUserId(token);
            request.setAttribute("userName", userName);
            request.setAttribute("userId", userId);

            // 8. 放行
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Token验证异常: {}", e.getMessage());
            sendUnauthorizedResponse(response, MessageConstant.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for(Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message, ErrorCode errorCode)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + errorCode.getCode() + ",\"message\":\"" + message + "\"}");
    }
}