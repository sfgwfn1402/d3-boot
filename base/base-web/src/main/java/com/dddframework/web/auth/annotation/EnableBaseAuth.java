package com.dddframework.web.auth.annotation;

import com.dddframework.web.auth.config.BaseAuthConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(BaseAuthConfig.class)
public @interface EnableBaseAuth {
}
