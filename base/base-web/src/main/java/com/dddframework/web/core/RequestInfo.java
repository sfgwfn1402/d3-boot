package com.dddframework.web.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestInfo {
    private String url;
    private Object params;
    private Map<String, Object> context;
}