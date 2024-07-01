package com.dddframework.demo.infras.repository.elasticsearch.entity;

import com.dddframework.data.elasticsearch.annotation.OnUpdate;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * es PO
 */
@Data
@NoArgsConstructor
public class EsDocUserPO extends EsPO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 成绩分值
     * 注：小数测试
     */
    private BigDecimal grades;

    /**
     * 创建时间
     */
    @OnUpdate
    private String createTime;
}
