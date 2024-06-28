package com.dddframework.demo.domain.Index.repository;

import com.dddframework.core.elasticsearch.contract.BaseRepository;
import com.dddframework.demo.domain.contract.query.EsIndexUserQuery;
import com.dddframework.demo.domain.Index.model.EsIndexUserModel;

public interface EsIndexUserRepository extends BaseRepository<EsIndexUserModel, EsIndexUserQuery> {
}
