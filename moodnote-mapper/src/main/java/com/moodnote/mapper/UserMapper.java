package com.moodnote.mapper;

import com.moodnote.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User selectByUsername(@Param("username") String username);

    User selectByEmail(@Param("email") String email);

    User selectByUsernameOrEmail(@Param("username") String username);

    void updateLastLoginTime(@Param("id") Long id);
}