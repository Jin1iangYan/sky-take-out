package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO 微信登录数据传输对象
     * @return 用户实体类
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
