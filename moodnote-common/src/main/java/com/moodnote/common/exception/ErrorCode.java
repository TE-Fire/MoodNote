package com.moodnote.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    BUSINESS_ERROR(501, "业务逻辑错误"),
    AI_SERVICE_ERROR(502, "AI 服务错误"),
    EMAIL_SERVICE_ERROR(503, "邮件服务错误"),
    CODE_TO_IMAGE_ERROR(504, "验证码转换为图片失败"),
    CODE_EXPIRE(505, "验证码已过期，请重新获取"),
    CODE_ERROR(506, "验证码错误");
    
    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
