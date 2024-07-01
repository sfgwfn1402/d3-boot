package com.dddframework.data.mongodb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "base-data")
public class BaseDataProperties {
    private Boolean printSql = false;
}