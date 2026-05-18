package com.moodnote.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.moodnote.common.utils.PageResult;
import com.moodnote.common.utils.Result;
import com.moodnote.mapper.DiaryMapper;
import com.moodnote.mapper.TagMapper;
import com.moodnote.pojo.dto.DiaryQueryDTO;
import com.moodnote.pojo.entity.Diary;
import com.moodnote.pojo.vo.DiaryVO;
import com.moodnote.pojo.vo.TagVO;
import com.moodnote.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiaryServiceImpl implements DiaryService {

    @Autowired
    private DiaryMapper diaryMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public Result<PageResult<DiaryVO>> getList(DiaryQueryDTO diaryQueryDTO) {
        // 获取当前用户ID（实际项目中应从Token或Session中获取）
        Long userId = getCurrentUserId();

        // 设置默认分页参数
        int pageNum = diaryQueryDTO.getPageNum() != null ? diaryQueryDTO.getPageNum() : 1;
        int pageSize = diaryQueryDTO.getPageSize() != null ? diaryQueryDTO.getPageSize() : 10;

        // 使用PageHelper进行分页
        PageHelper.startPage(pageNum, pageSize);

        // 查询日记列表
        List<Diary> diaryList = diaryMapper.selectDiaryList(
                userId,
                diaryQueryDTO.getKeyword(),
                diaryQueryDTO.getMoodType(),
                diaryQueryDTO.getWeatherType(),
                diaryQueryDTO.getStartDate(),
                diaryQueryDTO.getEndDate()
        );

        // 获取分页信息
        Page<Diary> page = (Page<Diary>) diaryList;
        long total = page.getTotal();

        // 转换为VO列表
        List<DiaryVO> voList = diaryList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建分页结果
        PageResult<DiaryVO> pageResult = PageResult.build(total, pageSize, pageNum, voList);

        return Result.success(pageResult);
    }

    /**
     * 将Diary实体转换为DiaryVO
     */
    private DiaryVO convertToVO(Diary diary) {
        DiaryVO vo = new DiaryVO();
        vo.setId(diary.getId());
        vo.setTitle(diary.getTitle());
        vo.setContent(diary.getContent());
        vo.setMoodType(diary.getMoodType());
        vo.setWeatherType(diary.getWeatherType());
        vo.setCity(diary.getCity());
        vo.setIsPrivate(diary.getIsPrivate());
        vo.setCreateTime(diary.getCreateTime());
        vo.setUpdateTime(diary.getUpdateTime());

        // 查询标签列表
        List<TagVO> tags = tagMapper.selectTagsByDiaryId(diary.getId());
        vo.setTags(tags);

        return vo;
    }

    /**
     * 获取当前用户ID（实际项目中应从Token中解析）
     */
    private Long getCurrentUserId() {
        // TODO: 实际实现应从JWT Token中解析用户ID
        // 这里为了演示，返回固定值
        return 1L;
    }
}
