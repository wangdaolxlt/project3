package com.mall.user.services;

import com.mall.user.IKaptchaService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dal.entitys.ImageResult;
import com.mall.user.dto.KaptchaCodeRequest;
import com.mall.user.dto.KaptchaCodeResponse;
import com.mall.user.utils.ExceptionProcessorUtils;
import com.mall.user.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 *  ciggar
 * create-date: 2019/8/6-14:45
 */
@Slf4j
@Component
@Service
public class KaptchaServiceImpl implements IKaptchaService {

    @Autowired
    RedissonClient redissonClient;

    private final String KAPTCHA_UUID="kaptcha_uuid";


    @Override
    public KaptchaCodeResponse getKaptchaCode(KaptchaCodeRequest request) {
        KaptchaCodeResponse response=new KaptchaCodeResponse();
        try {
            ImageResult capText = VerifyCodeUtils.VerifyCode(140, 43, 4);
            response.setImageCode(capText.getImg());
            String uuid= UUID.randomUUID().toString();
            RBucket rBucket=redissonClient.getBucket(KAPTCHA_UUID+uuid);
            rBucket.set(capText.getCode());
            log.info("产生的验证码:{},uuid:{}",capText.getCode(),uuid);
            rBucket.expire(120, TimeUnit.SECONDS);
            response.setImageCode(capText.getImg());
            response.setUuid(uuid);
            response.setCode(SysRetCodeConstants.SUCCESS.getCode());
            response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        }catch (Exception e){
            log.error("KaptchaServiceImpl.getKaptchaCode occur Exception :"+e);
            ExceptionProcessorUtils.wrapperHandlerException(response,e);
        }
        return response;
    }

    @Override
    public KaptchaCodeResponse validateKaptchaCode(KaptchaCodeRequest request) {
        KaptchaCodeResponse response=new KaptchaCodeResponse();
        try{
            request.requestCheck();
            // 从redis根据以uuid作为key获得验证码的值
            String redisKey = KAPTCHA_UUID+request.getUuid();
            RBucket<String> rBucket=redissonClient.getBucket(redisKey);
            String code=rBucket.get();
            log.info("请求的redisKey={},请求的code={},从redis获得的code={}",redisKey,request.getCode(),code);
            // 上传的值和redis中的值相等, 返回正确代码和信息
            if(StringUtils.isNotBlank(code)&&request.getCode().equalsIgnoreCase(code)){
                response.setCode(SysRetCodeConstants.SUCCESS.getCode());
                response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
                return response;
            }
            // 上传的值和redis中的值不相等, 返回错误代码和信息
            response.setCode(SysRetCodeConstants.KAPTCHA_CODE_ERROR.getCode());
            response.setMsg(SysRetCodeConstants.KAPTCHA_CODE_ERROR.getMessage());
        }catch (Exception e){
            log.error("KaptchaServiceImpl.validateKaptchaCode occur Exception :"+e);
            // 设置错误代码和错误信息
            ExceptionProcessorUtils.wrapperHandlerException(response,e);
        }
        return response;
    }
}
