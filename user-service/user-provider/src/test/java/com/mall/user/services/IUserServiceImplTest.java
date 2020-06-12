package com.mall.user.services;

import com.mall.user.bootstrap.UserProviderApplicationTests;
import com.mall.user.dto.UserRegisterRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @Author: Lucas_Alison
 * Date: 2020/6/11 23:15
 */

public class IUserServiceImplTest extends UserProviderApplicationTests {

    @Autowired
    IUserServiceImpl iUserService;


    @Test
    public void register() {
    }

    @Test
    public void sendEmail() {

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUserName("lisi");
        userRegisterRequest.setEmail("ytudjq@163.com");
        iUserService.sendEmail("123", userRegisterRequest);
    }

    @Test
    public void login() {
    }

    @Test
    public void validToken() {
    }
}