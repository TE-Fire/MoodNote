package com.moodnote.common.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private long total;
    private int pageSize;
    private int pageNum;
    private List<T> list;

    public PageResult(long total, int pageSize, int pageNum, List<T> list) {
        this.total = total;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.list = list;
    }

    public static <T> PageResult<T> build(long total, int pageSize, int pageNum, List<T> list) {
        return new PageResult<>(total, pageSize, pageNum, list);
    }
}
