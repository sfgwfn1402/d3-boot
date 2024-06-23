package com.dddframework.web.auth.interceptor;

import com.dddframework.core.context.SpringContext;
import com.dddframework.core.context.ThreadContext;
import com.dddframework.core.contract.exception.ServiceException;
import com.dddframework.core.utils.BizAssert;
import com.dddframework.web.auth.annotation.BaseAuth;
import com.dddframework.web.auth.annotation.Inside;
import com.dddframework.web.config.BaseWebProperties;
import com.dddframework.web.interceptor.BaseWebInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

import static com.dddframework.core.contract.constant.ContextConstants.AUTHORIZATION;
import static com.dddframework.core.contract.enums.ResultCode.FORBIDDEN;

@Slf4j(topic = "### BASE-WEB : BaseAuthInterceptor ###")
public class BaseAuthWebInterceptor extends BaseWebInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler.getClass() == ResourceHttpRequestHandler.class) return Boolean.TRUE;
        Method method = ((HandlerMethod) handler).getMethod();
        //判断访问的方法是否添加@Inside注解
        Inside insideMethod = ((HandlerMethod) handler).getMethodAnnotation(Inside.class);
        if (insideMethod != null) {
            return Boolean.TRUE;
        }
        BaseAuth baseAuth = method.getDeclaringClass().getDeclaredAnnotation(BaseAuth.class);
        if (baseAuth != null) {
            String bearerToken = ThreadContext.get(AUTHORIZATION);
            BizAssert.notBlank(bearerToken, "bearerToken不能为空");
            BaseWebProperties baseWebProperties = SpringContext.getBean(BaseWebProperties.class);
            if (!baseWebProperties.getAuth().getBearerTokens().contains(bearerToken.replaceFirst("Bearer ", ""))) {
                log.error("bearerTokens: {}", baseWebProperties.getAuth().getBearerTokens());
                throw new ServiceException(FORBIDDEN);
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public int getOrder() {
        return -199;
    }
}