package com.dddframework.demo.domain.contract.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 用户注册命令
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterCommand {
    // 客户昵称
    private String nickname;
    // 手机号
    @NotBlank(message = "手机号不能为空")
    private String phone;
}
