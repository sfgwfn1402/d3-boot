package com.dddframework.demo.domain.contract.command;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * es doc user command
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EsDocUserCommand {
    /**
     * 索引
     */
    private String index;
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
    private String createTime;

    public static void main(String[] args) {
        EsDocUserCommand command = EsDocUserCommand.builder()
               .index("user")
               .userId("123456")
               .userName("张三")   // 这里的userName是String类型
               .mobile("13800138000")
               .age(25)
               .grades(new BigDecimal("99.99"))
               .createTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"))
               .build();
        System.out.println(JSON.toJSONString(command));
    }
}
