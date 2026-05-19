package com.moodnote.service;

import com.moodnote.common.utils.PageResult;
import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.DiaryCreateDTO;
import com.moodnote.pojo.dto.DiaryQueryDTO;
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
    
}
