package com.moodnote.common.constant;

public class MessageConstant {

    public static final String EMAIL_SEND_SUCCESS = "邮件发送成功";
    public static final String EMAIL_SEND_FAILED = "邮件发送失败";
    public static final String EMAIL_EXISTED = "邮箱已存在";
    public static final String EMAIL_FORMAT_ERROR = "邮箱格式不正确";
    public static final String EMAIL_EMPTY = "邮箱不能为空";

    public static final String CODE_SEND_SUCCESS = "验证码发送成功，请查收邮件";
    public static final String CODE_SEND_FAILED = "发送失败，请稍后重试";
    public static final String CODE_EXPIRE = "验证码已过期，请重新获取";
    public static final String CODE_ERROR = "验证码错误";

    public static final String CODE_TO_IMAGE_ERROR = "验证码转换为图片失败，请联系管理员";
    public static final String CODE_TO_IMAGE_SUCCESS = "验证码转换为图片成功";

    public static final String USERNAME_EXISTED = "用户名已存在";
    public static final String REGISTER_SUCCESS = "注册成功";
    public static final String PASSWORD_RESET_SUCCESS = "密码重置成功";

    public static final String UNAUTHORIZED = "未授权，请先登录";
    public static final String TOKEN_BLACKLIST = "用户已登出，请重新登录";
    public static final String USERNAME_NOT_FOUND = "用户名不存在";
    public static final String PASSWORD_ERROR = "密码错误";

    public static final String LOGIN = "login";
    public static final String RESET = "reset";
    public static final String REGISTER = "register";

    public static final String LOGOUT_SUCCESS = "退出登录成功";
    public static final String LOGOUT_FAILED = "退出登录失败";

    public static final String REQUEST_ATTRIBUTE_ERROR = "请求属性为空";

    // =======update===============
    public static final String UPDATE_PROFILE_ERROR = "更新用户信息失败";
    public static final String UPDATE_PROFILE_SUCCESS = "更新用户信息成功";

    // ===========autoFillAspect==================
    public static final String INSERT_AUTO_FILL_ERROR = "插入操作AOP自动填充失败";
    public static final String UPDATE_AUTO_FILL_ERROR = "更新操作AOP自动填充失败";


}