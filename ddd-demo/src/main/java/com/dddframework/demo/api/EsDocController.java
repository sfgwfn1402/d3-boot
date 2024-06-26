package com.dddframework.demo.api;

import com.dddframework.core.context.ThreadContext;
import com.dddframework.core.contract.constant.ContextConstants;
import com.dddframework.core.utils.BizAssert;
import com.dddframework.demo.application.service.UserAppService;
import com.dddframework.demo.domain.Index.service.EsIndexService;
import com.dddframework.demo.domain.contract.command.EsDocUserCommand;
import com.dddframework.demo.domain.contract.command.EsIndexCommand;
import com.dddframework.demo.domain.contract.command.UserRegisterCommand;
import com.dddframework.demo.domain.document.service.EsDocUserService;
import com.dddframework.demo.domain.user.model.UserModel;
import com.dddframework.demo.domain.user.service.UserService;
import com.dddframework.web.api.AggregateController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * es doc controller
 */
@RestController
@RequestMapping({"/es_doc"})
@RequiredArgsConstructor
public class EsDocController implements AggregateController {
    final EsDocUserService esService;

    /**
     * 创建索引
     *
     * @param command
     * @return
     */
    @PostMapping("/save")
    public boolean save(@Valid @RequestBody EsDocUserCommand command) {
        return esService.save(command.getIndex(), command);
    }


}
