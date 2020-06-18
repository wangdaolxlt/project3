package com.mall.promo.cache;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 *  ciggar
 * create-date: 2019/7/24-17:17
 */
@Component
public class CacheManager {

    @Autowired
    private  RedissonClient redissonClient;

    // 获取value
    public  String checkCache(String key){
        try {
            RBucket rBucket = redissonClient.getBucket(key);
            return rBucket.get().toString();
        }catch (Exception e){
            return null;
        }
    }

    // 设置缓存
    public void setCache(String key,String val,int expire){
        RBucket rBucket = redissonClient.getBucket(key);
        rBucket.set(val);
        rBucket.expire(expire, TimeUnit.DAYS);
    }

    // 设置缓存过期时间
    public void expire(String key,int expire){
        RBucket rBucket = redissonClient.getBucket(key);
        rBucket.expire(expire,TimeUnit.DAYS);
    }

}
