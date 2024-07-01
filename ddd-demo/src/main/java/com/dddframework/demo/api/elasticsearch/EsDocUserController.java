package com.dddframework.demo.api.elasticsearch;

import com.dddframework.demo.domain.contract.command.EsDocUserCommand;
import com.dddframework.demo.domain.elasticsearch.document.service.EsDocUserService;
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
public class EsDocUserController implements AggregateController {
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
