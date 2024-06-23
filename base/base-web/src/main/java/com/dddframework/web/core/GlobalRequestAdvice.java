package com.dddframework.web.core;

import com.dddframework.core.context.ThreadContext;
import com.dddframework.core.contract.constant.ContextConstants;
import com.dddframework.core.utils.BeanKit;
import com.dddframework.core.utils.JsonKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

@RestControllerAdvice
public class GlobalRequestAdvice implements RequestBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        return httpInputMessage;
    }

    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        Map<String, Object> objectMap = BeanKit.toMapClean(o);
        String body = JsonKit.toJson(objectMap);
        ThreadContext.set(ContextConstants.REQUEST_BODY, body);
        Logger logger = LoggerFactory.getLogger("==> RequestBody");
        logger.info("{}", body);
        return o;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }
}
