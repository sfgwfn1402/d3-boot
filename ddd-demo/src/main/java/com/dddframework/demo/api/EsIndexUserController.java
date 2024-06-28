package com.dddframework.demo.api;

import com.dddframework.core.context.ThreadContext;
import com.dddframework.core.contract.constant.ContextConstants;
import com.dddframework.core.utils.BizAssert;
import com.dddframework.demo.application.service.UserAppService;
import com.dddframework.demo.domain.contract.command.EsIndexUserCommand;
import com.dddframework.demo.domain.contract.command.UserRegisterCommand;
import com.dddframework.demo.domain.user.model.UserModel;
import com.dddframework.demo.domain.Index.service.EsIndexUserService;
import com.dddframework.demo.domain.user.service.UserService;
import com.dddframework.web.api.AggregateController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * es索引
 */
@RestController
@RequestMapping({"/es_index"})
@RequiredArgsConstructor
public class EsIndexUserController implements AggregateController {
    final UserAppService userAppService;
    final UserService userService;
    final EsIndexUserService esService;

    /**
     * 创建索引
     *
     * @param command
     * @return
     */
    @PostMapping("/create_index")
    public boolean createIndex(@Valid @RequestBody EsIndexUserCommand command) {
        return esService.createIndex(command);
    }

    /**
     * 注册
     *
     * @param command
     * @return
     */
    @PostMapping("/user/register")
    public UserModel register(@Valid @RequestBody UserRegisterCommand command) {
        return userService.register(command);
    }

    /**
     * 修改
     *
     * @param user
     * @return
     */
    @PostMapping("/user/modify")
    public void modify(@RequestBody UserModel user) {
        BizAssert.notBlank(user.getId(), "ID不能为空");
        user.setRelateUserId(ThreadContext.get(ContextConstants.USER_ID));
        userAppService.modify(user);
    }

}
