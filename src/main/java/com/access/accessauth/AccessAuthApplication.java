package com.access.accessauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication //去掉数据源
@MapperScan(value = "com.access.accessauth.dao")
public class AccessAuthApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AccessAuthApplication.class, args);
	}

}
