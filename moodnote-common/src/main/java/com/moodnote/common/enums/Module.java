package com.moodnote.common.enums;

public enum Module {
    
    AUTH("auth", "认证模块"),
    USER("user", "用户模块"),
    DIARY("diary", "日记模块"),
    TAG("tag", "标签模块"),
    SYSTEM("system", "系统模块");
    
    private final String code;
    private final String description;
    
    Module(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
}
