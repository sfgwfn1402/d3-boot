package com.dddframework.demo.domain.user.service;

import com.dddframework.core.contract.exception.ServiceException;
import com.dddframework.core.utils.BeanKit;
import com.dddframework.demo.domain.contract.command.UserRegisterCommand;
import com.dddframework.demo.domain.contract.event.UserRegisterEvent;
import com.dddframework.demo.domain.contract.query.UserQuery;
import com.dddframework.demo.domain.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    /**
     * 用户注册
     *
     * @param userRegisterCommand
     * @return
     */
    public User register(UserRegisterCommand userRegisterCommand) {
        User existUser = UserQuery.builder().phone(userRegisterCommand.getPhone()).build().one();
        if (existUser != null) {
            throw new ServiceException("该手机号已注册用户");
        }
        User user = BeanKit.copy(userRegisterCommand, User.class);
        user.save();
        user.fill(UserQuery.builder().fillFileValues(true).build());
        new UserRegisterEvent(user).publish();
        return user;
    }
}
