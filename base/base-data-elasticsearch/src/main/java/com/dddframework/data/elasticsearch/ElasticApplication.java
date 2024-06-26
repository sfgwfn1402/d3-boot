package com.dddframework.data.elasticsearch;

import com.dddframework.data.elasticsearch.repository.impl.ESBaseRepositoryImpl;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class ElasticApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticApplication.class, args);
    }


//    @Bean("searchHelper")
//    public ESBaseRepositoryImpl elasticSearchHelper(@Autowired RestHighLevelClient highLevelClient) {
//        return new ESBaseRepositoryImpl(highLevelClient);
//    }
}
