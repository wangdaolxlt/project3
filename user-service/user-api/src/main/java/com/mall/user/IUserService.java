package com.mall.user;

import com.mall.user.dto.*;

/**
 * @Author: Lucas_Alison
 * Date: 2020/6/11 13:25
 */
public interface IUserService {

    /**
     * 用户注册接口
     * @param userRegisterRequest 用户注册用请求dto
     * @return 用户注册用响应dto
     */
     UserRegisterResponse  register(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录接口
     * @param loginRequest
     * @return
     */
    UserLoginResponse login(UserLoginRequest loginRequest);

    CheckAuthResponse validToken(CheckAuthRequest checkAuthRequest);

    UserVerifyResponse verify(UserVerifyRequest userVerifyRequest);
}
