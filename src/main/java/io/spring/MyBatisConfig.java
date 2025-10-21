package io.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableTransactionManagement
@EnableScheduling
public class MyBatisConfig {}
