package com.dddframework.web.api;

import com.dddframework.core.contract.BaseRepository;
import com.dddframework.core.contract.Model;
import com.dddframework.core.contract.Page;
import com.dddframework.core.contract.Query;
import com.dddframework.core.utils.BeanKit;
import com.dddframework.core.utils.BizAssert;
import com.dddframework.core.utils.MappingKit;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 聚合控制器，实现该控制器的Controller，自带CRUD方法
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
public interface AggregateController {

    // 公共分页
    @PostMapping("/{modelName}/page")
    default Page<Model> postPage(@PathVariable("modelName") String modelName, @RequestBody Map<String, Object> query) {
        return convertQuery(matchModel(modelName), query).page();
    }

    // 公共分页
    @GetMapping("/{modelName}/page")
    default Page<Model> getPage(@PathVariable("modelName") String modelName,@RequestParam Map<String, Object> query) {
        return convertQuery(matchModel(modelName), query).page();
    }

    // 公共列表
    @PostMapping("/{modelName}/list")
    default List<Model> postList(@PathVariable("modelName") String modelName, @RequestBody Map<String, Object> query) {
        return convertQuery(matchModel(modelName), query).list();
    }

    // 公共列表
    @GetMapping("/{modelName}/list")
    default List<Model> getList(@PathVariable("modelName") String modelName,@RequestParam  Map<String, Object> query) {
        return convertQuery(matchModel(modelName), query).list();
    }

    // 公共详情
    @GetMapping("/{modelName}/detail")
    default Model detail(@PathVariable("modelName") String modelName,@RequestParam Map<String, Object> query) {
        return convertQuery(matchModel(modelName), query).first();
    }

    // 公共详情
    @GetMapping("/{modelName}/detail/{id}")
    default Model detail(@PathVariable("modelName") String modelName, @PathVariable("id") String id) {
        return BaseRepository.of(matchModel(modelName)).get(id);
    }

    // 公共创建
    @PostMapping({"/{modelName}/save", "/{modelName}/create"})
    default Model save(@PathVariable("modelName") String modelName, @RequestBody Map<String, Object> query) {
        Model model = convertModel(matchModel(modelName), query);
        if (model == null) return null;
        model.save();
        return model;
    }

    // 公共批量创建
    @PostMapping("/{modelName}/saveBatch")
    default void saveBatch(@PathVariable("modelName") String modelName, @RequestBody List<Map<String, Object>> params) {
        Class<Model> modelClass = matchModel(modelName);
        BaseRepository.of(modelClass).save(convertModels(modelClass, params));
    }

    // 公共修改
    @PostMapping({"/{modelName}/update", "/{modelName}/modify"})
    default void update(@PathVariable("modelName") String modelName, @RequestBody Map<String, Object> query) {
        Model model = convertModel(matchModel(modelName), query);
        if (model != null) {
            model.update();
        }
    }

    // 公共删除
    @PostMapping({"/{modelName}/delete/{id}", "/{modelName}/remove/{id}"})
    default void delete(@PathVariable("modelName") String modelName, @PathVariable("id") String id) {
        BaseRepository.of(matchModel(modelName)).delete(id);
    }

    // 通过模型名匹配模型类
    static Class<Model> matchModel(String modelName) {
        Class<Model> modelClass = MappingKit.get("MODEL_NAME", modelName);
        BizAssert.notNull(modelClass, "Model: {} not found", modelName);
        return modelClass;
    }

    // 通过模型类找到查询类，并把Map参数转换为查询参数
    @SneakyThrows
    static Query convertQuery(Class<Model> modelClass, Map<String, Object> queryMap) {
        Class<Query> queryClass = MappingKit.get("MODEL_QUERY", modelClass);
        BizAssert.notNull(queryClass, "Query not found");
        Query query = BeanKit.ofMap(queryMap, queryClass);
        if (query == null) {
            query = queryClass.newInstance();
        }
        return query;
    }

    // 通过Map参数转换为模型
    static Model convertModel(Class<Model> modelClass, Map<String, Object> modelMap) {
        return BeanKit.ofMap(modelMap, modelClass);
    }

    // 批量通过Map参数转换为模型
    static List<Model> convertModels(Class<Model> modelClass, List<Map<String, Object>> modelMaps) {
        if (modelMaps != null && !modelMaps.isEmpty()) {
            return modelMaps.stream().map(modelMap -> BeanKit.ofMap(modelMap, modelClass)).collect(Collectors.toList());
        }
        return null;
    }

}
