package com.dddframework.demo.application.service;

import com.dddframework.core.utils.BizAssert;
import com.dddframework.demo.domain.mysql.user.model.UserModel;
import org.springframework.stereotype.Service;

@Service
public class UserAppService {

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    public void modify(UserModel user) {
        UserModel existUser = UserModel.repository().get(user.getId());
        BizAssert.notNull(existUser);
        UserModel.builder().id(user.getId()).build().update();
    }
}
