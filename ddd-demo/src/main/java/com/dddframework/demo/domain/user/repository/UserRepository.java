package com.dddframework.demo.domain.user.repository;

import com.dddframework.core.contract.BaseRepository;
import com.dddframework.demo.domain.contract.query.UserQuery;
import com.dddframework.demo.domain.user.model.User;

public interface UserRepository extends BaseRepository<User, UserQuery> {
}
