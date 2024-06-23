package com.dddframework.monitor.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @RequestMapping("/health")
    public String healthCheck() {
        return "ok";
    }
}
