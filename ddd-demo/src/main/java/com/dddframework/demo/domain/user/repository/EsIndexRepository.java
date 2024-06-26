package com.dddframework.demo.domain.user.repository;

import com.dddframework.core.elasticsearch.contract.BaseRepository;
import com.dddframework.demo.domain.contract.query.EsIndexQuery;
import com.dddframework.demo.domain.Index.model.EsIndex;

public interface EsIndexRepository extends BaseRepository<EsIndex, EsIndexQuery> {
}
