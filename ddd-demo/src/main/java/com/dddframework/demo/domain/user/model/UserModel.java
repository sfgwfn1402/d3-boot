package com.dddframework.demo.domain.user.model;

import com.dddframework.core.contract.BaseRepository;
import com.dddframework.core.contract.Model;
import com.dddframework.demo.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserModel extends Model {
    private String id;
    private String tenantId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 用户编码
    private String userCode;
    // 关联用户ID
    private String relateUserId;
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
    /*以下为非表字段*/
    // 档案值列表
    private List<Object> fileValues;

    public static UserRepository repository() {
        return BaseRepository.of(UserModel.class);
    }
}
