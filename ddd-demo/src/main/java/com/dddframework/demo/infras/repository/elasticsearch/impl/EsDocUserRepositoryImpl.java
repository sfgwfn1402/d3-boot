package com.dddframework.demo.infras.repository.elasticsearch.impl;

import com.dddframework.data.elasticsearch.repository.impl.ESBaseRepositoryImpl;
import com.dddframework.demo.domain.contract.query.EsDocUserQuery;
import com.dddframework.demo.domain.contract.query.UserQuery;
import com.dddframework.demo.domain.elasticsearch.document.model.EsDocUserModel;
import com.dddframework.demo.domain.elasticsearch.document.repository.EsDocUserRepository;
import com.dddframework.demo.domain.mysql.user.model.UserModel;
import com.dddframework.demo.infras.repository.elasticsearch.entity.EsDocUserPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class EsDocUserRepositoryImpl extends ESBaseRepositoryImpl<EsDocUserModel, EsDocUserPO, EsDocUserQuery> implements EsDocUserRepository {

    public void fill(UserQuery query, List<UserModel> models) {
        for (UserModel user : models) {
            if (query.getFillFileValues()) {
                // 填充VO对象
                user.setFileValues(new ArrayList<>());
            }
        }
    }
}
