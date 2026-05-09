package com.moodnote.mapper;

import com.moodnote.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface UserMapper {

    // language=SQL
    @Select("select exists(select 1 from mood_user where email = #{email} and deleted = 0)")
    boolean existByEmail(@Param("email") String email);

    // language=SQL
    @Select("select exists(select 1 from mood_user where username = #{username} and deleted = 0)")
    boolean existByUsername(@Param("username") String username);

    int insert(@Param("user") User user);

    // language=SQL
    @Select("select * from mood_user where username = #{username} and deleted = 0")
	User selectByUsername(String username);

    // language=SQL
    @Select("select * from mood_user where email = #{email} and deleted = 0")
    User selectByEmail(String email);
}