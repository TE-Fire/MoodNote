package com.moodnote.service;


import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.UpdateProfileDTO;
import com.moodnote.pojo.vo.UserVO;

public interface UserService {

    /**
     * 获取用户信息
     * @return
     */
    Result<UserVO> getUserInfo(Long userId);

    /**
     * 更新用户信息
     * @param userId
     * @param updateProfileDTO
     * @return
     */
    Result<Void> updateProfile(UpdateProfileDTO updateProfileDTO, Long userId);
    
}
