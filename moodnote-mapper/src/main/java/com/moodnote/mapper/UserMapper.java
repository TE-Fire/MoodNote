package com.moodnote.mapper;

import com.moodnote.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface UserMapper {

    // language=SQL
    @Select("select exists(select 1 from mood_user where email = #{email} and deleted = 0)")
    boolean existByEmail(@Param("email") String email);

    // language=SQL
    @Select("select exists(select 1 from mood_user where username = #{username} and deleted = 0)")
    boolean existByUsername(@Param("username") String username);

    // language=SQL
    @Insert("INSERT INTO mood_user (email, password, nickname, username, avatar, phone, gender, create_time, update_time, deleted) " +
            "VALUES (#{email}, #{password}, #{nickname}, #{username}, #{avatar}, #{phone}, #{gender}, #{createTime}, #{updateTime}, #{deleted})")
    int insert(User user);

    // language=SQL
    @Select("select * from mood_user where username = #{username} and deleted = 0")
	User selectByUsername(String username);

    // language=SQL
    @Select("select * from mood_user where email = #{email} and deleted = 0")
    User selectByEmail(String email);

    // language=SQL
    @Update("update mood_user set password = #{newPassword} where email = #{email} and deleted = 0")
    void resetPassword(@Param("email") String email, @Param("newPassword") String newPassword);
}