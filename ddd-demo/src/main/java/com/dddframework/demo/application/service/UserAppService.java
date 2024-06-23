package com.dddframework.demo.application.service;

import com.dddframework.core.utils.BizAssert;
import com.dddframework.demo.domain.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserAppService {

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    public void modify(User user) {
        User existUser = User.repository().get(user.getId());
        BizAssert.notNull(existUser);
        User.builder().id(user.getId()).build().update();
    }
}
