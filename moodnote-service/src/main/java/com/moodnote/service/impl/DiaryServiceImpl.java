package com.moodnote.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.moodnote.common.constant.MessageConstant;
import com.moodnote.common.utils.PageResult;
import com.moodnote.common.utils.Result;
import com.moodnote.mapper.DiaryMapper;
import com.moodnote.mapper.DiaryTagMapper;
import com.moodnote.mapper.TagMapper;
import com.moodnote.pojo.dto.DiaryCreateDTO;
import com.moodnote.pojo.dto.DiaryQueryDTO;
import com.moodnote.pojo.entity.Diary;
import com.moodnote.pojo.vo.DiaryVO;
import com.moodnote.pojo.vo.TagVO;
import com.moodnote.service.DiaryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiaryServiceImpl implements DiaryService {

    @Autowired
    private DiaryMapper diaryMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private DiaryTagMapper diaryTagMapper;

    @Override
    public Result<PageResult<DiaryVO>> getList(DiaryQueryDTO diaryQueryDTO) {
        Long userId = getCurrentUserId();

        int pageNum = diaryQueryDTO.getPageNum() != null ? diaryQueryDTO.getPageNum() : 1;
        int pageSize = diaryQueryDTO.getPageSize() != null ? diaryQueryDTO.getPageSize() : 10;

        PageHelper.startPage(pageNum, pageSize);

        List<Diary> diaryList = diaryMapper.selectDiaryList(
                userId,
                diaryQueryDTO.getKeyword(),
                diaryQueryDTO.getMoodType(),
                diaryQueryDTO.getWeatherType(),
                diaryQueryDTO.getStartDate(),
                diaryQueryDTO.getEndDate()
        );

        Page<Diary> page = (Page<Diary>) diaryList;
        long total = page.getTotal();

        List<DiaryVO> voList = diaryList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        PageResult<DiaryVO> pageResult = PageResult.build(total, pageSize, pageNum, voList);

        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<DiaryVO> createDiary(DiaryCreateDTO diaryCreateDTO) {
        Long userId = getCurrentUserId();

        Diary diary = new Diary();
     
        BeanUtils.copyProperties(diaryCreateDTO, diary);
        diary.setUserId(userId);
        if (diary.getIsPrivate() == null) {
            diary.setIsPrivate(1);
        }

        int rows = diaryMapper.createDiary(diary);
        if (rows == 0) {
            return Result.error(MessageConstant.CREATE_DIARY_ERROR);
        }

        if (diaryCreateDTO.getTagIds() != null && !diaryCreateDTO.getTagIds().isEmpty()) {
            diaryTagMapper.batchInsert(diary.getId(), diaryCreateDTO.getTagIds());
        }

        DiaryVO diaryVO = convertToVO(diary);
        return Result.success(diaryVO);
    }

    private DiaryVO convertToVO(Diary diary) {
        DiaryVO vo = new DiaryVO();
        BeanUtils.copyProperties(diary, vo);

        List<TagVO> tags = tagMapper.selectTagsByDiaryId(diary.getId());
        vo.setTags(tags);

        return vo;
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}