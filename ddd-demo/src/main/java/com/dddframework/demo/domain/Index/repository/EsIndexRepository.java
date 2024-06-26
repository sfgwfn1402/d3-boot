package com.dddframework.demo.domain.Index.repository;

import com.dddframework.core.elasticsearch.contract.BaseRepository;
import com.dddframework.demo.domain.contract.query.EsIndexQuery;
import com.dddframework.demo.domain.Index.model.EsIndexModel;

public interface EsIndexRepository extends BaseRepository<EsIndexModel, EsIndexQuery> {
}
