package com.moodnote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moodnote.pojo.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {

    User selectByUsername(@Param("username") String username);

    User selectByEmail(@Param("email") String email);

    User selectByUsernameOrEmail(@Param("username") String username);

    void updateLastLoginTime(@Param("id") Long id);
}