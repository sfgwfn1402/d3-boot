package com.dddframework.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "base-web")
public class BaseWebProperties {
    private Log log = new Log();
    private Mvc mvc = new Mvc();
    private Auth auth = new Auth();

    @Data
    public static class Log {
        private String includes = "/**";
        private String excludes = "/error";
    }

    @Data
    public static class Mvc {
        private String timePattern = "yyyy-MM-dd HH:mm:ss";
    }

    @Data
    public static class Auth {
        /**
         * Bearer访问令牌列表
         */
        private List<String> bearerTokens = new ArrayList<>();
    }
}
