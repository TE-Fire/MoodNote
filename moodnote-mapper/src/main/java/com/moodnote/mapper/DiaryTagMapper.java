package com.moodnote.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiaryTagMapper {

    /**
     * 批量插入日记-标签关联
     * @param diaryId 日记ID
     * @param tagIds 标签ID列表
     * @return 影响行数
     */
    int batchInsert(@Param("diaryId") Long diaryId, @Param("tagIds") List<Long> tagIds);

    /**
     * 根据日记ID删除关联
     * @param diaryId 日记ID
     * @return 影响行数
     */
    int deleteByDiaryId(@Param("diaryId") Long diaryId);
}