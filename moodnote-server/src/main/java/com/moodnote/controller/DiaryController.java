package com.moodnote.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moodnote.common.utils.PageResult;
import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.DiaryCreateDTO;
import com.moodnote.pojo.dto.DiaryQueryDTO;
import com.moodnote.pojo.dto.DiaryUpdateDTO;
import com.moodnote.pojo.vo.DiaryVO;
import com.moodnote.service.DiaryService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/diary")
public class DiaryController {
    
    @Autowired
    private DiaryService diaryService;

    /**
     * 获取日记列表
     * @param diaryQueryDTO
     * @return
     */
    @GetMapping("/list")
    public Result<PageResult<DiaryVO>> getDiaryList(@ModelAttribute DiaryQueryDTO diaryQueryDTO) {
        log.info("获取日记列表，查询参数: {}", diaryQueryDTO);
        return diaryService.getList(diaryQueryDTO);
    }

    /**
     * 创建日记
     * @param diaryCreateDTO
     * @return
     */
    @PostMapping
    public Result<DiaryVO> createDiary(@RequestBody DiaryCreateDTO diaryCreateDTO) {
        log.info("创建日记");
        return diaryService.createDiary(diaryCreateDTO);
    }

    /**
     * 获取日记详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DiaryVO> getDiaryDetail(@PathVariable Long id) {
        log.info("获取日记详情, 日记id{}", id);
        return diaryService.getDetail(id);
    }

    /**
     * 更新日记
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    public Result<Void> updateDiary(@RequestBody DiaryUpdateDTO diaryUpdateDTO, @PathVariable Long id) {
        log.info("更新日记, 日记id{}", id);
        return diaryService.updateDiary(diaryUpdateDTO, id);
    }
}