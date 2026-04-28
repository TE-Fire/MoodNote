package com.moodnote.common.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private long total;
    private int pageSize;
    private int current;
    private int pages;
    private List<T> records;

    public PageResult(long total, int pageSize, int current, List<T> records) {
        this.total = total;
        this.pageSize = pageSize;
        this.current = current;
        this.records = records;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }

    public static <T> PageResult<T> build(long total, int pageSize, int current, List<T> records) {
        return new PageResult<>(total, pageSize, current, records);
    }
}