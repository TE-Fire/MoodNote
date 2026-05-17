package com.moodnote.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moodnote.common.annotation.LogOperation;
import com.moodnote.common.enums.Action;
import com.moodnote.common.enums.Module;
import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.UpdateProfileDTO;
import com.moodnote.pojo.vo.UserVO;
import com.moodnote.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public Result<UserVO> userInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("获取用户个人信息, 用户ID{}", userId);
        return userService.getUserInfo(userId);
    }

    @PutMapping("/profile")
    @LogOperation(module = Module.USER, action = Action.UPDATE_PROFILE)
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody UpdateProfileDTO updateProfileDTO) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("更新用户信息，用户ID: {}", userId);
        return userService.updateProfile(updateProfileDTO, userId);
    }
}
