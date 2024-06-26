package com.dddframework.demo.domain.document.repository;

import com.dddframework.core.elasticsearch.contract.BaseRepository;
import com.dddframework.demo.domain.contract.query.EsIndexQuery;
import com.dddframework.demo.domain.document.model.EsDocUserModel;

public interface EsDocUserRepository extends BaseRepository<EsDocUserModel, EsIndexQuery> {
}
