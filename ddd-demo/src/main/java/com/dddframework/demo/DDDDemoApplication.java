package com.dddframework.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableFeignClients
@EnableReactiveMongoRepositories(basePackages = "com.dddframework.data.mongodb.repository")
@SpringCloudApplication
public class DDDDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DDDDemoApplication.class, args);
    }


}
