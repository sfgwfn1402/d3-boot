package com.dddframework.demo.infras.repository.mysql.impl;

import com.dddframework.data.repository.impl.BaseRepositoryImpl;
import com.dddframework.demo.domain.contract.query.UserQuery;
import com.dddframework.demo.domain.mysql.user.model.UserModel;
import com.dddframework.demo.domain.mysql.user.repository.UserRepository;
import com.dddframework.demo.infras.repository.mysql.dao.UserMapper;
import com.dddframework.demo.infras.repository.mysql.entity.UserPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserRepositoryImpl extends BaseRepositoryImpl<UserMapper, UserModel, UserPO, UserQuery> implements UserRepository {

    public void fill(UserQuery query, List<UserModel> models) {
        for (UserModel user : models) {
            if (query.getFillFileValues()) {
                // 填充VO对象
                user.setFileValues(new ArrayList<>());
            }
        }
    }

}
