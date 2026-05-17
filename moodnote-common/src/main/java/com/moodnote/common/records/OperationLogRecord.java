package com.moodnote.common.records;

import java.time.LocalDateTime;

/**
 * 操作日志记录，自动生成构造函数、getter、equals、hashCode、toString
 */
public record OperationLogRecord(
    Long userId,
    String module,
    String action,
    String description,
    String requestUri,
    String requestMethod,
    LocalDateTime createTime
) {}
