package com.dddframework.web.auth.interceptor;

import com.dddframework.core.context.BaseContext;
import com.dddframework.core.context.ThreadContext;
import com.dddframework.core.contract.constant.ContextConstants;
import com.dddframework.core.contract.exception.ServiceException;
import com.dddframework.web.auth.annotation.Inside;
import com.dddframework.web.core.SessionContext;
import com.dddframework.web.interceptor.BaseWebInterceptor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.dddframework.core.contract.constant.ContextConstants.*;
import static com.dddframework.core.contract.enums.ResultCode.*;

/**
 * 客户端接口拦截器，拦截以/api、/client开头的接口
 */
public class ApiWebInterceptor extends BaseWebInterceptor {
    public static final String THIRD_SESSION_PREFIX = "app:3rd_session:";
    public static final String CLIENT_TYPE_H5_WX = "H5-WX";
    private static final long TIME_OUT_SESSION = 24 * 5;
    public static final List<String> CLIENT_TYPES = Arrays.asList("H5", "tenant-app", "A-MA", "BP-H5-PC", "H5-PC", "supply-pc", "APP", "BP-H5", "admin");

    @Override
    public String[] pathPatterns() {
        return new String[]{"/api/**", "/client/**"};
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler.getClass() == ResourceHttpRequestHandler.class) return Boolean.TRUE;
        HandlerMethod method = (HandlerMethod) handler;
        //判断访问的Controller或方法是否添加@Inside注解
        Inside insideType = method.getMethod().getDeclaringClass().getDeclaredAnnotation(Inside.class);
        Inside insideMethod = method.getMethodAnnotation(Inside.class);
        if (insideType != null || insideMethod != null) {
            return Boolean.TRUE;
        }
        //获取header中的客户端类型
        String clientType = ThreadContext.get(CLIENT_TYPE);
        String appId = ThreadContext.get(APP_ID);
        //普通h5端、没有appId的微信H5、APP：只对@ApiLogin注解的接口进行Session校验
        boolean notH5Client = clientType != null && !CLIENT_TYPES.contains(clientType) && (!CLIENT_TYPE_H5_WX.equals(clientType) || (appId != null && !appId.isEmpty()));
        if (notH5Client) {
            //小程序端或带有appId的微信H5的所有接口需要登录才能访问，校验thirdSession
            String thirdSession = ThreadContext.get(THIRD_SESSION);
            if (thirdSession == null || thirdSession.isEmpty()) {
                throw new ServiceException(NO_SESSION);
            }
            // 校验session
            SessionContext sessionContext = ThreadContext.get(SESSION_CONTEXT);
            //判断session是否过期，或session是否属于当前tenantId、appId
            if (sessionContext == null || !sessionContext.getTenantId().equals(ThreadContext.get(TENANT_ID)) || ((sessionContext.getAppId() != null && !sessionContext.getAppId().isEmpty()) && !sessionContext.getAppId().equals(appId))) {
                throw new ServiceException(TIMEOUT);
            }
            // 必须登录商城才能访问
            if (sessionContext.getUserId() == null || sessionContext.getUserId().isEmpty()) {
                throw new ServiceException(LOGIN_FIRST);
            }
        }
        // Session续期
        if (ThreadContext.contains(SESSION_CONTEXT)) {
            RedisTemplate redisTemplate = BaseContext.get(ContextConstants.REDIS_TEMPLATE);
            if (redisTemplate != null) {
                redisTemplate.expire(THIRD_SESSION_PREFIX + ThreadContext.get(THIRD_SESSION), TIME_OUT_SESSION, TimeUnit.HOURS);
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public int getOrder() {
        return -200;
    }
}