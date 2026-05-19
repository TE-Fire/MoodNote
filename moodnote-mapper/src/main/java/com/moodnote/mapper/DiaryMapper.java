package com.moodnote.mapper;

import com.moodnote.pojo.entity.Diary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiaryMapper {

    List<Diary> selectDiaryList(@Param("userId") Long userId,
                                @Param("keyword") String keyword,
                                @Param("moodType") Integer moodType,
                                @Param("weatherType") Integer weatherType,
                                @Param("startDate") String startDate,
                                @Param("endDate") String endDate);

    Diary selectDiaryById(@Param("id") Long id, @Param("userId") Long userId);

    int createDiary(Diary diary);
}