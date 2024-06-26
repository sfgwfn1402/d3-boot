package com.dddframework.demo.infras.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dddframework.data.elasticsearch.annotation.BizKey;
import lombok.Data;

/**
 * 用户
 */
@Data
@TableName("demo_user")
public class UserPO extends PO {
    // 客户编码
    @BizKey
    private String userCode;
    // 关联用户ID
    private String userId;
    // 客户昵称
    private String nickname;
    // 头像
    private String avatar;
    // 姓名
    private String realName;
    // 性别 1.男 2.女 0.未知
    private String sex;
    // 年龄
    private Integer age;
    // 手机号
    private String phone;
    // 是否生效 1.生效 0.失效
    private Boolean enable;
}
