package com.dddframework.web.interceptor;

import com.dddframework.web.config.BaseWebProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 日志Web拦截器
 *
 * @func 打印请求路径、请求方法
 */
public class LogWebInterceptor extends BaseWebInterceptor {
    @Autowired
    BaseWebProperties baseWebProperties;
    final ThreadLocal<LocalDateTime> beginTime = new ThreadLocal<>();

    @Override
    public String[] pathPatterns() {
        return baseWebProperties.getLog().getIncludes().split(",");
    }

    @Override
    public String[] excludePathPatterns() {
        return baseWebProperties.getLog().getExcludes().split(",");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler.getClass() == ResourceHttpRequestHandler.class) return Boolean.TRUE;
        HandlerMethod method = (HandlerMethod) handler;
        String className = method.getBeanType().getSimpleName();
        String methodName = method.getMethod().getName();
        Logger logger = LoggerFactory.getLogger("==> Request");
        logger.info("{} -> {}.{}()", request.getRequestURI(), className, methodName);
        beginTime.set(LocalDateTime.now());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        try {
            Logger logger = LoggerFactory.getLogger(String.format("<== Response in %s ms", Duration.between(beginTime.get(), LocalDateTime.now()).toMillis()));
            logger.info(request.getRequestURI());
        } catch (Exception ignored) {
        } finally {
            beginTime.remove();
        }

    }

    @Override
    public int getOrder() {
        return -500;
    }
}