package com.gymcrm.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.gymcrm")
@EntityScan(basePackages = "com.gymcrm.domain")
@PropertySource({"classpath:application.properties", "classpath:application-local.properties"})
@EnableTransactionManagement
public class AppConfig {
}
