package com.dddframework.demo.domain.Index.service;

import com.dddframework.core.utils.BeanKit;
import com.dddframework.demo.domain.Index.model.EsIndexUserModel;
import com.dddframework.demo.domain.contract.command.EsIndexUserCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EsIndexUserService {

    /**
     * 创建索引
     * @param esCommand
     * @return
     */
    public boolean createIndex(EsIndexUserCommand esCommand) {
        EsIndexUserModel index = BeanKit.copy(esCommand, EsIndexUserModel.class);
        boolean f = index.createIndex(esCommand.getProperties());
        log.info("createIndex result: {}", f);
        return f;
    }
}
