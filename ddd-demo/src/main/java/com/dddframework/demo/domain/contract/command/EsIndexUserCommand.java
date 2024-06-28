package com.dddframework.demo.domain.contract.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * es index command
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EsIndexUserCommand {
    // 客户昵称
    private String index;

    // mapping信息
    private Map<String, Object> properties;
}
