package com.mall.user.services;

import com.alibaba.fastjson.JSON;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.user.IUserService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.converter.MemberConverter;
import com.mall.user.dal.entitys.Member;
import com.mall.user.dal.entitys.UserVerify;
import com.mall.user.dal.persistence.MemberMapper;
import com.mall.user.dal.persistence.UserVerifyMapper;
import com.mall.user.dto.*;
import com.mall.user.utils.JwtTokenUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @Author: Lucas_Alison
 * Date: 2020/6/11 13:29
 */
@Component
@Service
@Slf4j
@ConfigurationProperties(prefix = "verify.mail")
@Data
public class IUserServiceImpl implements IUserService {
    @Autowired
    MemberMapper memberMapper;
    @Autowired
    UserVerifyMapper userVerifyMapper;
    @Autowired
    MemberConverter memberConverter;
    @Autowired
    JavaMailSender javaMailSender;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 发件人
     */
    private String addresser;
    /**
     * 内容前缀
     */
    private String msgPrefix;

    /**
     * 用户注册接口
     *
     * @param registerRequest 用户注册用请求dto
     * @return 用户注册用响应dto
     */
    @Override
    @Transactional
    public UserRegisterResponse register(UserRegisterRequest registerRequest) {
        UserRegisterResponse registerResponse = new UserRegisterResponse();
        // 数据校验
        registerRequest.requestCheck();
        // 验证用户名是否重复
        validUserNameRepeat(registerRequest);

        // 1. 向用户表中插入一条记录
        Member memberRecord = Member.builder()
                .username(registerRequest.getUserName())
                .password(DigestUtils.md5DigestAsHex(registerRequest.getUserPwd().getBytes()))
                .created(new Date())
                .updated(new Date())
                .isVerified("N")
                .state(1)
                .build();
        int insertMemberEffectiveRows = memberMapper.insert(memberRecord);
        if (insertMemberEffectiveRows != 1) {
            // 插入数据失败
            registerResponse.setCode(SysRetCodeConstants.USER_REGISTER_FAILED.getCode());
            registerResponse.setMsg(SysRetCodeConstants.USER_REGISTER_FAILED.getMessage());
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return registerResponse;
        }

        // 2. 向用户验证表中插入一条记录
        UserVerify userVerifyRecord = UserVerify.builder()
                .username(registerRequest.getUserName())
                .registerDate(new Date())
                .uuid(DigestUtils.md5DigestAsHex((registerRequest.getUserName() + registerRequest.getEmail() + UUID.randomUUID()).getBytes()))
                .isVerify("N")
                .isExpire("N")
                .build();
        int insertUserVerifyEffectiveRows = userVerifyMapper.insert(userVerifyRecord);
        if (insertUserVerifyEffectiveRows != 1) {
            // 插入数据失败
            registerResponse.setCode(SysRetCodeConstants.USER_REGISTER_FAILED.getCode());
            registerResponse.setMsg(SysRetCodeConstants.USER_REGISTER_FAILED.getMessage());
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return registerResponse;
        }

        // 3. 发送用户激活邮件
        // TODO: 2020/6/11  发送用户激活邮件, 消息中间件MQ优化
        sendEmail(userVerifyRecord.getUuid(), registerRequest);

        // 用户插入成功
        registerResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        registerResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return registerResponse;
    }

    /**
     * 发送用户激活邮件
     *
     * @param uuid
     * @param registerRequest
     */
    public void sendEmail(String uuid, UserRegisterRequest registerRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setFrom(addresser);
        // message.setSubject("CSMALL");
        // message.setFrom("lucas_tung@yeah.net");
        message.setTo(registerRequest.getEmail());
        message.setText(msgPrefix + uuid + "&username=" + registerRequest.getUserName());
        // message.setText("http://localhost:8080/user/verify?uid=" + uuid + "&username=" + registerRequest.getUserName());

        javaMailSender.send(message);

    }

    /**
     * 验证username 是否重复
     *
     * @param userRegisterRequest
     */
    private void validUserNameRepeat(UserRegisterRequest userRegisterRequest) {
        Example memberExample = new Example(Member.class);
        memberExample.createCriteria().andEqualTo("username", userRegisterRequest.getUserName());
        List<Member> memberList = memberMapper.selectByExample(memberExample);
        if (!CollectionUtils.isEmpty(memberList)) {
            throw new ValidateException(SysRetCodeConstants.USERNAME_ALREADY_EXISTS.getCode(),
                    SysRetCodeConstants.USERNAME_ALREADY_EXISTS.getMessage());
        }
    }

    /**
     * 用户登录接口
     *
     * @param loginRequest
     * @return
     */
    @Override
    public UserLoginResponse login(UserLoginRequest loginRequest) {
        loginRequest.requestCheck();
        UserLoginResponse loginResponse = new UserLoginResponse();
        Example memberExample = new Example(Member.class);
        memberExample.createCriteria().andEqualTo("username", loginRequest.getUserName());
        List<Member> memberList = memberMapper.selectByExample(memberExample);
        if (CollectionUtils.isEmpty(memberList)) {
            // 不存在该用户
            loginResponse.setCode(SysRetCodeConstants.USERORPASSWORD_ERRROR.getCode());
            loginResponse.setMsg(SysRetCodeConstants.USERORPASSWORD_ERRROR.getMessage());
            return loginResponse;
        }

        // 用户名验证通过
        Member memberRecord = memberList.get(0);

        // 用户是否已激活
        if ("N".equals(memberRecord.getIsVerified())) {
            loginResponse.setCode(SysRetCodeConstants.USER_ISVERFIED_ERROR.getCode());
            loginResponse.setMsg(SysRetCodeConstants.USER_ISVERFIED_ERROR.getMessage());
            return loginResponse;
        }

        String passwordFromDb = memberRecord.getPassword();
        String passwordFromFrontEnd = DigestUtils.md5DigestAsHex(loginRequest.getPassword().getBytes());
        if (!passwordFromFrontEnd.equals(passwordFromDb)) {
            // 密码错误
            loginResponse.setCode(SysRetCodeConstants.USERORPASSWORD_ERRROR.getCode());
            loginResponse.setMsg(SysRetCodeConstants.USERORPASSWORD_ERRROR.getMessage());
            return loginResponse;
        }

        // 用户验证通过, 将user
        loginResponse = memberConverter.member2UserLoginResponse(memberRecord);
        loginResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        loginResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());

        // 用于token的map
        HashMap<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("uid", memberRecord.getId());
        tokenMap.put("file", memberRecord.getFile());
        tokenMap.put("username", memberRecord.getUsername());
        // 产生token
        String token = JwtTokenUtils.builder().msg(JSON.toJSONString(tokenMap)).build().creatJwtToken();
        loginResponse.setToken(token);

        return loginResponse;
    }

    @Override
    public CheckAuthResponse validToken(CheckAuthRequest checkAuthRequest) {
        CheckAuthResponse checkAuthResponse = new CheckAuthResponse();
        checkAuthRequest.requestCheck();
        String tokenString = JwtTokenUtils.builder().token(checkAuthRequest.getToken()).build().freeJwt();
        if (StringUtils.isBlank(tokenString)) {
            checkAuthResponse.setCode(SysRetCodeConstants.TOKEN_VALID_FAILED.getCode());
            checkAuthResponse.setMsg(SysRetCodeConstants.TOKEN_VALID_FAILED.getMessage());
            return checkAuthResponse;
        }

        // token信息正确, 返回
        checkAuthResponse.setUserinfo(tokenString);
        checkAuthResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        checkAuthResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return checkAuthResponse;

    }

    @Override
    @Transactional
    public UserVerifyResponse verify(UserVerifyRequest userVerifyRequest) {
        userVerifyRequest.requestCheck();
        UserVerifyResponse userVerifyResponse = new UserVerifyResponse();

        Example userVerifyExample = new Example(UserVerify.class);
        userVerifyExample.createCriteria().andEqualTo("uuid", userVerifyRequest.getUuid()).andEqualTo("username",
                userVerifyRequest.getUserName());
        UserVerify userVerify = new UserVerify();
        userVerify.setIsVerify("Y");
        int updateUserVerifyEffectiveRows = userVerifyMapper.updateByExampleSelective(userVerify, userVerifyExample);
        if (updateUserVerifyEffectiveRows == 0) {
            // 参数错误, 不存在该用户
            userVerifyResponse.setCode(SysRetCodeConstants.USERVERIFY_INFOR_INVALID.getCode());
            userVerifyResponse.setMsg(SysRetCodeConstants.USERVERIFY_INFOR_INVALID.getMessage());
            return userVerifyResponse;
        }
        // 存在该用户
        Example memberExample = new Example(Member.class);
        memberExample.createCriteria().andEqualTo("username", userVerifyRequest.getUserName());
        Member memberRecord = new Member();
        memberRecord.setIsVerified("Y");
        int updateMemberEffectiveRows = memberMapper.updateByExampleSelective(memberRecord, memberExample);
        if(updateMemberEffectiveRows == 0){
            // member表不存在该数据
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            userVerifyResponse.setCode(SysRetCodeConstants.DB_EXCEPTION.getCode());
            userVerifyResponse.setMsg(SysRetCodeConstants.DB_EXCEPTION.getMessage());
            return userVerifyResponse;
        }
        userVerifyResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        userVerifyResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return userVerifyResponse;
    }
}
