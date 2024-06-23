package com.dddframework.web.core;

import com.dddframework.core.context.ThreadContext;
import com.dddframework.core.contract.constant.ContextConstants;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * 本地化Resolver
 */
public class BaseAcceptHeaderLocaleResolver extends AcceptHeaderLocaleResolver {

    public BaseAcceptHeaderLocaleResolver() {
        super();
        // 设置默认为简体中文
        this.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
    }

    /**
     * 把请求的Locale设进本地线程，用于后续处理
     *
     * @param request 请求
     * @return 本地化对象
     */
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = super.resolveLocale(request);
        ThreadContext.set(ContextConstants.LOCALE, locale);
        return locale;
    }
}
