package com.dddframework.web.api;

import com.dddframework.core.contract.BaseRepository;
import com.dddframework.core.contract.Model;
import com.dddframework.core.contract.Page;
import com.dddframework.core.contract.Query;
import com.dddframework.web.utils.ReflectKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class CRUDController<M extends Model, Q extends Query> {
    private static final Logger log = LoggerFactory.getLogger("### BASE-WEB : CRUDController ###");
    protected BaseRepository<M, Q> repository;

    private BaseRepository<M, Q> getRepository() {
        if (this.repository == null) {
            Class<M> modelClass = ReflectKit.getSuperClassGenericType(this.getClass(), 0);
            this.repository = BaseRepository.of(modelClass);
        }
        if (this.repository == null) {
            log.error("未找到实体仓库");
        }
        return repository;
    }

    @PostMapping("/page")
    public Page<M> page(@RequestBody Q query) {
        return query.page();
    }

    @GetMapping("/page")
    public Page<M> getPage(Q query) {
        return query.page();
    }

    @PostMapping("/list")
    public List<M> list(@RequestBody Q query) {
        return query.list();
    }

    @GetMapping("/list")
    public List<M> getList(Q query) {
        return query.list();
    }

    @GetMapping("/detail")
    public M detail(Q query) {
        return query.first();
    }

    @GetMapping("/detail/{id}")
    public M detail(@PathVariable("id") String id) {
        return getRepository().get(id);
    }

    @PostMapping({"/save", "/create"})
    public M save(@RequestBody M model) {
        model.save();
        return model;
    }

    @PostMapping("/saveBatch")
    public void saveBatch(@RequestBody List<M> models) {
        getRepository().save(models);
    }

    @PostMapping({"/update", "/modify"})
    public void update(@RequestBody M model) {
        model.update();
    }

    @PostMapping({"/delete/{id}", "/remove/{id}"})
    public void delete(@PathVariable("id") String id) {
        getRepository().delete(id);
    }
}
