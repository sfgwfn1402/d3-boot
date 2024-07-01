package com.dddframework.demo.domain.mongodb.document.service;

import com.dddframework.core.contract.exception.ServiceException;
import com.dddframework.core.mongodb.contract.Model;
import com.dddframework.core.mongodb.utils.BeanKit;
import com.dddframework.demo.domain.contract.command.MongoDocCityCommand;
import com.dddframework.demo.domain.contract.command.UserRegisterCommand;
import com.dddframework.demo.domain.contract.event.UserRegisterEvent;
import com.dddframework.demo.domain.contract.query.MongoDocCityQuery;
import com.dddframework.demo.domain.contract.query.UserQuery;
import com.dddframework.demo.domain.mongodb.document.model.MongoDocCityModel;
import com.dddframework.demo.domain.mysql.user.model.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class MongoDocCityService {

    /**
     * 保存城市信息
     *
     * @param cityCommand
     * @return
     */
    public Model save(MongoDocCityCommand cityCommand) {
        MongoDocCityModel model = BeanKit.copy(cityCommand, MongoDocCityModel.class);
        Model save = (Model) model.save();
//        log.info("user save result: {}", save.block());
        return save;
    }


    public UserModel register(UserRegisterCommand userRegisterCommand) {
        UserModel existUser = UserQuery.builder().phone(userRegisterCommand.getPhone()).build().one();
        if (existUser != null) {
            throw new ServiceException("该手机号已注册用户");
        }
        UserModel user = com.dddframework.core.utils.BeanKit.copy(userRegisterCommand, UserModel.class);
        user.save();
        user.fill(UserQuery.builder().fillFileValues(true).build());
        new UserRegisterEvent(user).publish();
        return user;
    }

    public MongoDocCityModel findCityById(Long id) {
        MongoDocCityModel existUser = MongoDocCityQuery.builder().build().findById(id);
        return existUser;
    }

//    public Flux<City> findAllCity() {
//
//        return cityRepository.findAll();
//    }
//
//    public Mono<City> modifyCity(City city) {
//
//        return cityRepository.save(city);
//    }
//
//    public Mono<Long> deleteCity(Long id) {
//        cityRepository.deleteById(id);
//        return Mono.create(cityMonoSink -> cityMonoSink.success(id));
//    }
//
//    public Mono<City> getByCityName(String cityName) {
//        return cityRepository.findByCityName(cityName);
//    }
}
