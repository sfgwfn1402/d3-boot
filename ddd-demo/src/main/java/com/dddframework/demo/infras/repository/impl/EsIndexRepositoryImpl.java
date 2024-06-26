package com.dddframework.demo.infras.repository.impl;

import com.dddframework.data.elasticsearch.repository.impl.ESBaseRepositoryImpl;
import com.dddframework.demo.domain.Index.model.EsIndex;
import com.dddframework.demo.domain.contract.query.EsIndexQuery;
import com.dddframework.demo.domain.contract.query.UserQuery;
import com.dddframework.demo.domain.user.model.User;
import com.dddframework.demo.domain.user.repository.EsIndexRepository;
import com.dddframework.demo.infras.repository.entity.EsIndexPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class EsIndexRepositoryImpl extends ESBaseRepositoryImpl<EsIndex, EsIndexPO, EsIndexQuery> implements EsIndexRepository {
//    @Bean("searchHelper")
//    public ESBaseRepositoryImpl elasticSearchHelper(@Autowired RestHighLevelClient highLevelClient) {
//        return new ESBaseRepositoryImpl(highLevelClient);
//    }

    public void fill(UserQuery query, List<User> models) {
        for (User user : models) {
            if (query.getFillFileValues()) {
                // 填充VO对象
                user.setFileValues(new ArrayList<>());
            }
        }
    }
}
