package com.mall.promo.bootstrap;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author lenovo
 * @PackgeName: PromoProviderApplication
 * @date: 2020/6/18
 * @Description:
 */
@MapperScan(basePackages = "com.mall.promo.dal")
@ComponentScan(basePackages = "com.mall.promo")
@SpringBootApplication
public class PromoProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(PromoProviderApplication.class, args);
    }
}
