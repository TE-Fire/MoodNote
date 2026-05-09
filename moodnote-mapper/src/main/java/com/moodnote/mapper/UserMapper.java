package com.moodnote.mapper;

import com.moodnote.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
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

    // language=SQL
    @Insert("insert into mood_user (username, password, email, nickname, avatar, gender, create_time, update_time) " +
            "values (#{user.username}, #{user.password}, #{user.email}, #{user.nickname}, #{user.avatar}, #{user.gender}, #{user.createTime}, #{user.updateTime})")
    int insert(@Param("user") User user);
}