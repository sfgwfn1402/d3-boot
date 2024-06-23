package com.dddframework.demo.domain.erp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ERP用户信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErpUser implements Serializable {
    // 应用方关联用户ID（自定义），长度建议在8位以内，后面相关接口均可使用此ID关联
    private String extId;
    // 姓名
    private String name;
    // 性别
    private String sex;
    // 年龄
    private Integer age;
    // 性别：1男，2女
    private Integer gender;
}
