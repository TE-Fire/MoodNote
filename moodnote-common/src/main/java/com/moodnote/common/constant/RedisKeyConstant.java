package com.moodnote.common.constant;

/**
 * Redis Key 常量类
 * 采用层级结构设计：{项目前缀}:{模块}:{操作}:{唯一标识}
 */
public class RedisKeyConstant {

    private static final String PROJECT_PREFIX = "moodnote";

    private static final String AUTH_MODULE = "auth";

    private static final String VERIFICATION = "verification";

    private static final String BLACKLIST = "blacklist";

    public static String getVerificationKey(String type, String email) {
        return PROJECT_PREFIX + ":" + AUTH_MODULE + ":" + VERIFICATION + ":" + type + ":" + email;
    }

    public static String getRegisterCodeKey(String email) {
        return getVerificationKey("register", email);
    }

    public static String getLoginCodeKey(String email) {
        return getVerificationKey("login", email);
    }

    public static String getResetCodeKey(String email) {
        return getVerificationKey("reset", email);
    }

    public static String getUserTokenKey(Long userId) {
        return PROJECT_PREFIX + ":" + AUTH_MODULE + ":token:" + userId;
    }

    public static String getUserSessionKey(String sessionId) {
        return PROJECT_PREFIX + ":" + AUTH_MODULE + ":session:" + sessionId;
    }

    public static String getCaptchaKey(String uuid) {
        return PROJECT_PREFIX + ":" + AUTH_MODULE + ":captcha:" + uuid;
    }

    public static String getBlacklistKey(String token) {
        return PROJECT_PREFIX + ":" + AUTH_MODULE + ":" + BLACKLIST + ":" + token;
    }
}
