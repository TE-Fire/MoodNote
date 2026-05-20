package com.moodnote.service;

import com.moodnote.common.utils.PageResult;
import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.DiaryCreateDTO;
import com.moodnote.pojo.dto.DiaryQueryDTO;
import com.moodnote.pojo.dto.DiaryUpdateDTO;
import com.moodnote.pojo.vo.DiaryVO;

public interface DiaryService {

    /**
     * 获取日记列表
     * @param diaryQueryDTO
     * @return
     */
    Result<PageResult<DiaryVO>> getList(DiaryQueryDTO diaryQueryDTO);

    /**
     * 创建日记
     * @param diaryCreateDTO
     * @return
     */
    Result<DiaryVO> createDiary(DiaryCreateDTO diaryCreateDTO);

    /**
     * 获取日记详情
     * @param id
     * @return
     */
    Result<DiaryVO> getDetail(Long id);

    Result<Void> updateDiary(DiaryUpdateDTO diaryUpdateDTO, Long id);
    
}
