package com.dddframework.demo.infras.repository.mongodb.impl;

import com.dddframework.data.mongodb.repository.impl.MongoBaseRepositoryImpl;
import com.dddframework.demo.domain.contract.query.MongoDocCityQuery;
import com.dddframework.demo.domain.contract.query.UserQuery;
import com.dddframework.demo.domain.mongodb.document.model.MongoDocCityModel;
import com.dddframework.demo.domain.mongodb.document.repository.MongoDocCityRepository;
import com.dddframework.demo.domain.mysql.user.model.UserModel;
import com.dddframework.demo.infras.repository.mongodb.entity.MongoDocCityPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MongoDocCityRepositoryImpl extends MongoBaseRepositoryImpl<MongoDocCityModel, MongoDocCityPO, MongoDocCityQuery> implements MongoDocCityRepository {


    public void fill(UserQuery query, List<UserModel> models) {
        for (UserModel user : models) {
            if (query.getFillFileValues()) {
                // 填充VO对象
                user.setFileValues(new ArrayList<>());
            }
        }
    }
}
