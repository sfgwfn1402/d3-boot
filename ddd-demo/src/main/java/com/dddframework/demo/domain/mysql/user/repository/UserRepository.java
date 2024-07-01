package com.dddframework.demo.domain.mysql.user.repository;

import com.dddframework.core.contract.BaseRepository;
import com.dddframework.demo.domain.contract.query.UserQuery;
import com.dddframework.demo.domain.mysql.user.model.UserModel;

public interface UserRepository extends BaseRepository<UserModel, UserQuery> {
}
