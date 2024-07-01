package com.dddframework.demo.domain.elasticsearch.document.repository;

import com.dddframework.core.elasticsearch.contract.BaseRepository;
import com.dddframework.demo.domain.contract.query.EsDocUserQuery;
import com.dddframework.demo.domain.elasticsearch.document.model.EsDocUserModel;

public interface EsDocUserRepository extends BaseRepository<EsDocUserModel, EsDocUserQuery> {
}
