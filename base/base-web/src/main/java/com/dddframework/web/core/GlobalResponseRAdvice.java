package com.dddframework.web.core;

import com.dddframework.core.contract.IR;
import com.dddframework.core.contract.R;
import com.dddframework.core.contract.exception.ServiceException;
import com.dddframework.web.annotation.RawResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalResponseRAdvice implements ResponseBodyAdvice<Object> {
    @Autowired
    @Qualifier("mvcObjectMapper")
    ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> aClass) {
        return !(IR.class.isAssignableFrom(returnType.getParameterType()) || returnType.hasMethodAnnotation(RawResponse.class));
    }

    @Override
    public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (returnType.getParameterType().isAssignableFrom(void.class)) {
            return R.ok();
        }
        // String类型不能直接包装
        if (returnType.getGenericParameterType().equals(String.class)) {
            try {
                //将数据包装在ResultVo里后转换为json串进行返回
                return objectMapper.writeValueAsString(R.ok(data));
            } catch (JsonProcessingException e) {
                throw new ServiceException(e);
            }
        }
        //否则直接包装成R对象返回
        return R.ok(data);
    }
}
