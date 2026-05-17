package com.moodnote.common.enums;

public enum Action {
    // 通用操作
    CREATE("create", "创建"),
    READ("read", "查询"),
    UPDATE("update", "更新"),
    DELETE("delete", "删除"),
    
    // 认证相关
    LOGIN("login", "登录"),
    LOGOUT("logout", "登出"),
    REGISTER("register", "注册"),
    RESET_PASSWORD("reset_password", "重置密码"),
    
    // 用户相关
    UPDATE_PROFILE("update_profile", "更新个人信息"),
    GET_INFO("get_info", "获取用户信息"),
    
    // 日记相关
    CREATE_DIARY("create_diary", "创建日记"),
    UPDATE_DIARY("update_diary", "更新日记"),
    DELETE_DIARY("delete_diary", "删除日记"),
    QUERY_DIARY("query_diary", "查询日记"),
    
    // 标签相关
    CREATE_TAG("create_tag", "创建标签"),
    UPDATE_TAG("update_tag", "更新标签"),
    DELETE_TAG("delete_tag", "删除标签");
    
    private final String code;
    private final String description;
    
    Action(String code, String description) {
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
