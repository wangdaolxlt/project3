package com.mall.pay.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @PackgeName: com.mall.pay.bootstrap
 * @ClassName: PayProviderApplication
 * @Author: Li Haiquan
 * Date: 2020/6/20 20:49
 * project name: cs-mall
 */

@MapperScan(basePackages = "com.mall.pay.dal")
@ComponentScan(basePackages = "com.mall.pay")
@SpringBootApplication
public class PayProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayProviderApplication.class, args);
    }
}
