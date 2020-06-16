package com.mall.promo.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @PackgeName: com.mall.promo.bootstrap
 * @ClassName: PromoApplication
 * @Author: Li Haiquan
 * Date: 2020/6/16 21:12
 * project name: cs-mall
 */
@MapperScan(basePackages = "com.mall.promo.dal")
@ComponentScan(basePackages = "com.mall.promo")
@SpringBootApplication
public class PromoProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(PromoProviderApplication.class, args);
    }
}
