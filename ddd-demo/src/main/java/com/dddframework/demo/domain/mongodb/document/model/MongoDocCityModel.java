package com.dddframework.demo.domain.mongodb.document.model;

import com.dddframework.core.mongodb.contract.BaseRepository;
import com.dddframework.core.mongodb.contract.Model;
import com.dddframework.demo.domain.mongodb.document.repository.MongoDocCityRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * mongodb index
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "city")
public class MongoDocCityModel extends Model {

    /**
     * 城市编号
     */
    @Id
    private Long id;

    /**
     * 省份编号
     */
    private Long provinceId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 描述
     */
    private String description;

    public static MongoDocCityRepository repository() {
        return BaseRepository.of(MongoDocCityModel.class);
    }
}
