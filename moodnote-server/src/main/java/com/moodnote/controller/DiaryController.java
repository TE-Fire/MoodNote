package com.moodnote.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moodnote.common.utils.PageResult;
import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.DiaryQueryDTO;
import com.moodnote.pojo.vo.DiaryVO;
import com.moodnote.service.DiaryService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/diary")
public class DiaryController {
    
    @Autowired
    private DiaryService diaryService;

    public Result<PageResult<DiaryVO>> getDiaryList(@RequestBody DiaryQueryDTO diaryQueryDTO) {
        log.info("获取日记列表，查询参数: {}", diaryQueryDTO);
        return diaryService.getList(diaryQueryDTO);
    }
}
