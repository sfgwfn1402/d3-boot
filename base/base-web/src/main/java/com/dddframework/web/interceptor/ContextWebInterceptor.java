package com.dddframework.web.interceptor;

import com.dddframework.core.context.BaseContext;
import com.dddframework.core.context.ThreadContext;
import com.dddframework.core.contract.constant.ContextConstants;
import com.dddframework.core.utils.JsonKit;
import com.dddframework.web.core.SessionContext;
import com.dddframework.web.utils.IpUtils;
import com.dddframework.web.utils.RequestContext;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.dddframework.core.contract.constant.ContextConstants.*;

/**
 * 上下文Web拦截器
 *
 * @func 获取参数请求头，设到线程上下文以便后续使用
 */
public class ContextWebInterceptor extends BaseWebInterceptor {
    public static final String THIRD_SESSION_PREFIX = "app:3rd_session:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ThreadContext.set(REQUEST_URL, RequestContext.getUri());
        ThreadContext.set(REQUEST_PARAMS, RequestContext.getParams());
        String tenantId = (request.getHeader("tenant_id") != null && !request.getHeader("tenant_id").isEmpty()) ? request.getHeader("tenant_id") : (request.getHeader("tenant-id") != null && !request.getHeader("tenant-id").isEmpty()) ? request.getHeader("tenant-id") : request.getHeader("tenantId");
        if (tenantId != null && !tenantId.isEmpty()) {
            ThreadContext.set(TENANT_ID, tenantId);
        }
        ThreadContext.set(SYSTEM_ID, request.getHeader(SYSTEM_ID));
        ThreadContext.set(THIRD_SESSION, request.getHeader(THIRD_SESSION));
        ThreadContext.set(CLIENT_TYPE, request.getHeader(CLIENT_TYPE));
        ThreadContext.set(APP_ID, request.getHeader(APP_ID));
        ThreadContext.set(SHOP_ID, request.getHeader(SHOP_ID));
        ThreadContext.set(IP, IpUtils.getIp(request));
        ThreadContext.set(AUTHORIZATION, request.getHeader(AUTHORIZATION));
        if (request.getHeader(THIRD_SESSION) != null && !request.getHeader(THIRD_SESSION).isEmpty()) {
            //获取缓存中的ThirdSession
            RedisTemplate redisTemplate = BaseContext.get(ContextConstants.REDIS_TEMPLATE);
            if (redisTemplate != null) {
                Object thirdSessionObj = redisTemplate.opsForValue().get(THIRD_SESSION_PREFIX + request.getHeader(THIRD_SESSION));
                if (thirdSessionObj != null) {
                    SessionContext sessionContext = JsonKit.toObject(String.valueOf(thirdSessionObj), SessionContext.class);
                    ThreadContext.set(SESSION_CONTEXT, sessionContext);
                    ThreadContext.set(USER_ID, sessionContext.getUserId());
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadContext.clear();
    }

    @Override
    public int getOrder() {
        return -400;
    }
}