package com.dddframework.demo.domain.document.service;

import com.dddframework.core.utils.BeanKit;
import com.dddframework.demo.domain.contract.command.EsDocUserCommand;
import com.dddframework.demo.domain.document.model.EsDocUserModel;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EsDocUserService {

    /**
     * 创建索引
     * @param esCommand
     * @return
     */
    public boolean save(String index, EsDocUserCommand esCommand) {
        EsDocUserModel model = BeanKit.copy(esCommand, EsDocUserModel.class);
        IndexResponse f = model.save(index);
        log.info("user save result: {}", f.getResult());
        return true;
    }
}
