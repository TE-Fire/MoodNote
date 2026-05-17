package com.moodnote.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moodnote.common.constant.MessageConstant;
import com.moodnote.common.exception.BusinessException;
import com.moodnote.common.exception.ErrorCode;
import com.moodnote.common.utils.Result;
import com.moodnote.mapper.UserMapper;
import com.moodnote.pojo.dto.UpdateProfileDTO;
import com.moodnote.pojo.entity.User;
import com.moodnote.pojo.vo.UserVO;
import com.moodnote.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService{
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<UserVO> getUserInfo(Long userId) {
        User user = userMapper.getUserById(userId);
        UserVO userVO = new UserVO();
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, MessageConstant.USERNAME_NOT_FOUND);
        }
        BeanUtils.copyProperties(user, userVO);
        return Result.success(userVO);
    }


    @Override
    public Result<Void> updateProfile(UpdateProfileDTO updateProfileDTO, Long userId) {
        if (userMapper.updateProfile(updateProfileDTO, userId)) {
            return Result.success(MessageConstant.UPDATE_PROFILE_SUCCESS);
        } else {
            return Result.error(MessageConstant.UPDATE_PROFILE_ERROR);
        }
    }
}
