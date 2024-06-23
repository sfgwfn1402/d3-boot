package com.dddframework.monitor.domain.contract.dto;

import lombok.Data;

import java.util.List;

/**
 * dingding at
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
@Data
public class AtDTO {

    private List<String> atMobiles;
    private Boolean isAtAll = false;
}