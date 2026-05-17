package com.moodnote.common.constant;

import com.moodnote.common.enums.Module;
/**
 * Redis Key 常量类
 * 采用层级结构设计：{项目前缀}:{模块}:{操作}:{唯一标识}
 */
public class RedisKeyConstant {

    private static final String PROJECT_PREFIX = "moodnote";

    private static final String AUTH_MODULE = "auth";

    private static final String VERIFICATION = "verification";

    private static final String BLACKLIST = "blacklist";

    private static final String OPERATION_LOG = "op_log";

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

     /**
     * 获取操作日志的 Redis Key（动态模块）
     * 
     * @param module 模块枚举
     * @param timestamp 时间戳（唯一标识）
     * @return Redis Key
     */
    public static String getOperationLogKey(Module module, long timestamp) {
        return PROJECT_PREFIX + ":" + OPERATION_LOG + ":" + module.getCode() + ":" + timestamp;
    }
    
    /**
     * 获取操作日志的 Redis Key（字符串模块名，兼容旧代码）
     */
    public static String getOperationLogKey(String moduleCode, long timestamp) {
        return PROJECT_PREFIX + ":" + OPERATION_LOG + ":" + moduleCode + ":" + timestamp;
    }
    
    /**
     * 获取某模块操作日志的前缀（用于批量查询）
     */
    public static String getOperationLogPrefix(Module module) {
        return PROJECT_PREFIX + ":" + OPERATION_LOG + ":" + module.getCode() + ":";
    }
}
