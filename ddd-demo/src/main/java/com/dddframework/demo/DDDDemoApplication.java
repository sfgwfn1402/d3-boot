package com.dddframework.demo;

import com.dddframework.data.elasticsearch.repository.impl.ESBaseRepositoryImpl;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableFeignClients
@SpringCloudApplication
public class DDDDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DDDDemoApplication.class, args);
    }



}
