package com.dddframework.monitor.domain.contract.dto;

import lombok.Data;

/**
 * Dingding msg
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
@Data
public class MsgDTO {
    private String msgtype;
    private TextDTO text;
    private AtDTO at;
    private MarkDownDTO markdown;
}