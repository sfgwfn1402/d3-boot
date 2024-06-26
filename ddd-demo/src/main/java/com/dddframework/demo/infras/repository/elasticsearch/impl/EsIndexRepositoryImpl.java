package com.dddframework.demo.infras.repository.elasticsearch.impl;

import com.dddframework.data.elasticsearch.repository.impl.ESBaseRepositoryImpl;
import com.dddframework.demo.domain.Index.model.EsIndexModel;
import com.dddframework.demo.domain.contract.query.EsIndexQuery;
import com.dddframework.demo.domain.contract.query.UserQuery;
import com.dddframework.demo.domain.user.model.UserModel;
import com.dddframework.demo.domain.Index.repository.EsIndexRepository;
import com.dddframework.demo.infras.repository.elasticsearch.entity.EsIndexPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class EsIndexRepositoryImpl extends ESBaseRepositoryImpl<EsIndexModel, EsIndexPO, EsIndexQuery> implements EsIndexRepository {

    public void fill(UserQuery query, List<UserModel> models) {
        for (UserModel user : models) {
            if (query.getFillFileValues()) {
                // 填充VO对象
                user.setFileValues(new ArrayList<>());
            }
        }
    }
}
