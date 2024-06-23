package com.dddframework.web.utils;

import com.dddframework.core.context.ThreadContext;
import com.dddframework.core.contract.constant.ContextConstants;
import com.dddframework.core.utils.JsonKit;
import lombok.experimental.UtilityClass;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class RequestContext {
    public HttpServletRequest get() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getRequest();
    }

    public String getUrl() {
        return get().getRequestURL().toString();
    }

    public String getUri() {
        return get().getRequestURI();
    }

    public String getParams() {
        if (ThreadContext.contains(ContextConstants.REQUEST_PARAMS)) {
            return ThreadContext.get(ContextConstants.REQUEST_PARAMS);
        }
        HttpServletRequest request = get();
        if (Objects.equals(request.getMethod(), "GET")) {
            if (request.getParameterMap() != null && !request.getParameterMap().isEmpty()) {
                return JsonKit.toJson(request.getParameterMap().entrySet());
            }
        } else {
            try {
                // 使用ContentCachingRequestWrapper包装原始请求
                ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
                return new String(requestWrapper.getContentAsByteArray());
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public Map<String, String> getHeaders() {
        HttpServletRequest request = get();
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        return headers;
    }

    public String getHeader(String header) {
        return get().getHeader(header);
    }

    public String getOrDefault(String header, String defaultValue) {
        String o = getHeader(header);
        if (o == null) return defaultValue;
        return o;
    }

    public Integer getOrDefault(String header, Integer defaultValue) {
        try {
            String headerValue = getHeader(header);
            if (headerValue == null) {
                return defaultValue;
            }
            return Integer.valueOf(headerValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
