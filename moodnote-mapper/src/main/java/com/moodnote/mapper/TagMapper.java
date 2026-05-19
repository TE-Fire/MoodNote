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

    /**
     * 根据多个日记ID批量查询标签列表
     * @param diaryIds
     * @return
     */
    List<TagVO> selectTagsByDiaryIds(@Param("diaryIds") List<Long> diaryIds);
}
