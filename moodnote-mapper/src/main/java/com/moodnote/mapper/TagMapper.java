package com.moodnote.mapper;

import com.moodnote.pojo.vo.TagVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper {

    /**
     * 根据日记ID查询关联的标签列表
     * @param diaryId
     * @return
     */
    List<TagVO> selectTagsByDiaryId(@Param("diaryId") Long diaryId);
}
