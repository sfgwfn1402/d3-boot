package com.dddframework.demo.domain.mongodb.document.repository;

import com.dddframework.core.mongodb.contract.BaseRepository;
import com.dddframework.demo.domain.contract.query.MongoDocCityQuery;
import com.dddframework.demo.domain.mongodb.document.model.MongoDocCityModel;

public interface MongoDocCityRepository extends BaseRepository<MongoDocCityModel, MongoDocCityQuery> {
}
