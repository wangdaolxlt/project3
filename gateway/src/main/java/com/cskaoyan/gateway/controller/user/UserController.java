package com.cskaoyan.gateway.controller.user;

import com.alibaba.fastjson.JSON;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.commons.tool.utils.CookieUtil;
import com.mall.user.IKaptchaService;
import com.mall.user.IUserService;
import com.mall.user.annotation.Anoymous;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dto.*;
import com.mall.user.intercepter.TokenIntercepter;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: Lucas_Alison
 * Date: 2020/6/11 16:39
 */
@RequestMapping("user")
@RestController
public class UserController {

    @Reference(timeout = 3000, check = false)
    IKaptchaService iKaptchaService;
    @Reference(timeout = 3000, check = false)
    IUserService iUserService;

    /**
     * 用户注册接口
     * @param registerMap
     * @param httpServletRequest
     * @return
     */
    @PostMapping("register")
    @Anoymous
    public ResponseData register(@RequestBody Map<String, String> registerMap, HttpServletRequest httpServletRequest) {
        String userName = registerMap.get("userName");
        String userPwd = registerMap.get("userPwd");
        String captcha = registerMap.get("captcha");
        String email = registerMap.get("email");

        // 验证验证码是否正确
        KaptchaCodeRequest kaptchaCodeRequest = new KaptchaCodeRequest();
        String uuid = CookieUtil.getCookieValue(httpServletRequest, "kaptcha_uuid");
        kaptchaCodeRequest.setUuid(uuid);
        kaptchaCodeRequest.setCode(captcha);
        KaptchaCodeResponse response = iKaptchaService.validateKaptchaCode(kaptchaCodeRequest);
        // 验证不通过
        // TODO: 2020/6/11 验证不通过处理
        // if (!response.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
        if (!true) {
            return new ResponseUtil<>().setErrorMsg(response.getMsg());
        }

        // 验证通过, 向用户表插入记录
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUserName(userName);
        registerRequest.setUserPwd(userPwd);
        registerRequest.setEmail(email);

        UserRegisterResponse registerResponse = iUserService.register(registerRequest);

        if (SysRetCodeConstants.SUCCESS.getCode().equals(registerResponse.getCode())) {
            return new ResponseUtil().setData(null);
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());

    }

    /**
     * 用户登录接口
     * @param loginMap
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    @PostMapping("login")
    @Anoymous
    public ResponseData login(@RequestBody Map<String, String> loginMap, HttpServletRequest httpServletRequest,
                              HttpServletResponse httpServletResponse) {
        String userName = loginMap.get("userName");
        String userPwd = loginMap.get("userPwd");
        String captcha = loginMap.get("captcha");

        // 验证验证码是否正确
        KaptchaCodeRequest request = new KaptchaCodeRequest();
        String uuid = CookieUtil.getCookieValue(httpServletRequest, "kaptcha_uuid");
        request.setUuid(uuid);
        request.setCode(captcha);
        KaptchaCodeResponse response = iKaptchaService.validateKaptchaCode(request);
        // 验证不通过
        // TODO: 2020/6/11 验证不通过处理
        // if (!response.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
        if (!true) {
            return new ResponseUtil<>().setErrorMsg(response.getMsg());
        }

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUserName(userName);
        loginRequest.setPassword(userPwd);

        // 验证通过, 调用service进行密码验证
        UserLoginResponse loginResponse = iUserService.login(loginRequest);

        if (SysRetCodeConstants.SUCCESS.getCode().equals(loginResponse.getCode())) {
            // 用户登录成功, 将token存储在cookie中
            // cookie的存活时间单位是秒
            Cookie cookie = CookieUtil.genCookie(TokenIntercepter.ACCESS_TOKEN, loginResponse.getToken(), "/", 60 * 60);
            // 如果在Cookie中设置了"HttpOnly"属性，那么通过JavaScript脚本将无法读取到Cookie信息
            cookie.setHttpOnly(true);
            httpServletResponse.addCookie(cookie);
            return new ResponseUtil().setData(loginResponse);
        }

        // 用户登录成功
        return new ResponseUtil().setErrorMsg(loginResponse.getMsg());
    }

    /**
     * 验证用户登录接口
     * @return
     */
    @GetMapping("login")
    public ResponseData login(HttpServletRequest httpServletRequest){
        String userInfo = (String) httpServletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        Object data = JSON.parse(userInfo);

        return new ResponseUtil().setData(data);
    }

    @GetMapping("loginOut")
    public ResponseData logOut(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        Cookie[] cookies = httpServletRequest.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(TokenIntercepter.ACCESS_TOKEN)){
                cookie.setValue(null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                httpServletResponse.addCookie(cookie);
            }
        }

        return new ResponseUtil().setData(null);
    }

    @GetMapping("verify")
    @Anoymous
    public ResponseData verify(@RequestParam Map<String, String> verifyRequestMap){
        String uid = verifyRequestMap.get("uid");
        String username = verifyRequestMap.get("username");
        UserVerifyRequest userVerifyRequest = new UserVerifyRequest();
        userVerifyRequest.setUserName(username);
        userVerifyRequest.setUuid(uid);
        UserVerifyResponse userVerifyResponse = iUserService.verify(userVerifyRequest);
        if(!SysRetCodeConstants.SUCCESS.getCode().equals(userVerifyResponse.getCode())){
            // 激活失败
            return new ResponseUtil().setErrorMsg(userVerifyResponse.getMsg());
        }

        return new ResponseUtil().setData(null);
    }

}
