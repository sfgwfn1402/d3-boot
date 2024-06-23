package com.dddframework.web.core;

import com.dddframework.core.contract.exception.ServiceException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignErrorDecoderFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

@Slf4j(topic = "### BASE-WEB : GlobalFeignErrorAdvice ###")
public class GlobalFeignErrorAdvice implements FeignErrorDecoderFactory {

    @Override
    public ErrorDecoder create(Class<?> type) {
        return (methodKey, response) -> {
            String reason = response.reason();
            String serviceName = response.request().requestTemplate().feignTarget().name();
            int status = response.status();
            String errorMsg = String.format("status=%s,%s:%s", status, serviceName, reason == null || reason.isEmpty() ? "请求异常" : reason);
            if (response.body() != null) {
                try {
                    Reader reader = response.body().asReader(Charset.defaultCharset());
                    errorMsg = toString(reader);
                    return new ServiceException(errorMsg);
                } catch (Exception e) {
                    log.warn("", e);
                }
            }
            log.warn("\n Feign异常请求: \n\n{}", response.request().toString());
            log.warn("\n Feign异常响应: \n\n{}", response);
            return new ServiceException(errorMsg);
        };
    }

    public static String toString(Reader input) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[1024 * 4];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            stringBuilder.append(buffer, 0, n);
        }
        return stringBuilder.toString();
    }
}
