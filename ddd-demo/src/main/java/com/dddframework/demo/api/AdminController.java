package com.dddframework.demo.api;

import com.dddframework.web.api.AggregateController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端接口
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController implements AggregateController {
}