package com.dddframework.demo.domain.contract.query;

import com.dddframework.core.contract.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户查询
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserQuery extends Query {
    // 创建时间-开始
    private LocalDateTime createTimeStart;
    // 创建时间-结束
    private LocalDateTime createTimeEnd;
    // 用户ID列表
    private List<String> idIn;
    // 客户编码
    private String userCode;
    // 关联用户ID
    private String relateUserId;
    // 客户昵称-模糊
    // 姓名-模糊
    private String realNameLike;
    // 性别 1.男 2.女 0.未知
    private String sex;
    private String nicknameLike;
    // 手机号
    private String phone;
    // 手机号-模糊
    private String phoneLike;
    // 备注-模糊
    private String remarkLike;
    // 是否生效 1.生效 0.失效
    private Boolean enable;
    /*以下为非表字段*/
    // 查询档案值列表
    @Builder.Default
    private Boolean fillFileValues = false;
}
