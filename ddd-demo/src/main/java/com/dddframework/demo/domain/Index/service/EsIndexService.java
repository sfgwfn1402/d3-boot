package com.dddframework.demo.domain.Index.service;

import com.dddframework.core.utils.BeanKit;
import com.dddframework.demo.domain.Index.model.EsIndexModel;
import com.dddframework.demo.domain.contract.command.EsIndexCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EsIndexService {

    /**
     * 创建索引
     * @param esCommand
     * @return
     */
    public boolean createIndex(EsIndexCommand esCommand) {
        EsIndexModel index = BeanKit.copy(esCommand, EsIndexModel.class);
        boolean f = index.createIndex(esCommand.getProperties());
        log.info("createIndex result: {}", f);
        return f;
    }
}
