package com.moodnote.mapper;

import com.moodnote.pojo.entity.Diary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiaryMapper {

    /**
     * 分页查询日记列表（动态条件）
     * PageHelper会自动添加分页，无需手动写COUNT查询
     * @param userId
     * @param keyword
     * @param moodType
     * @param weatherType
     * @param startDate
     * @param endDate
     * @return
     */
    List<Diary> selectDiaryList(@Param("userId") Long userId,
                                @Param("keyword") String keyword,
                                @Param("moodType") Integer moodType,
                                @Param("weatherType") Integer weatherType,
                                @Param("startDate") String startDate,
                                @Param("endDate") String endDate);

    /**
     * 根据ID查询日记
     * @param id
     * @param userId
     * @return
     */
    Diary selectDiaryById(@Param("id") Long id, @Param("userId") Long userId);
}
