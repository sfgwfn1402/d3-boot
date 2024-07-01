package com.dddframework.demo.api.mongodb;

import com.dddframework.core.mongodb.contract.Model;
import com.dddframework.demo.domain.contract.command.EsDocUserCommand;
import com.dddframework.demo.domain.contract.command.MongoDocCityCommand;
import com.dddframework.demo.domain.mongodb.document.model.MongoDocCityModel;
import com.dddframework.demo.domain.mongodb.document.service.MongoDocCityService;
import com.dddframework.web.api.AggregateController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * mongo doc controller
 */
@RestController
@RequestMapping({"/mongo/city"})
@RequiredArgsConstructor
public class MongoCollectionCityController implements AggregateController {
    @Autowired
    private MongoDocCityService docCityService;

    @GetMapping(value = "/{id}")
    @ResponseBody
    public MongoDocCityModel findCityById(@PathVariable("id") Long id) {
        return docCityService.findCityById(id);
    }

    @PostMapping(value = "/save")
    @ResponseBody
    public Model saveCity(@RequestBody MongoDocCityCommand city) {
        Model save = docCityService.save(city);
        return save;
    }

//    @GetMapping()
//    @ResponseBody
//    public Flux<City> findAllCity() {
//        return cityHandler.findAllCity();
//    }
//
//
//    @PutMapping()
//    @ResponseBody
//    public Mono<City> modifyCity(@RequestBody City city) {
//        return cityHandler.modifyCity(city);
//    }
//
//    @DeleteMapping(value = "/{id}")
//    @ResponseBody
//    public Mono<Long> deleteCity(@PathVariable("id") Long id) {
//        return cityHandler.deleteCity(id);
//    }
//
//    private static final String CITY_LIST_PATH_NAME = "cityList";
//    private static final String CITY_PATH_NAME = "city";
//
//    @GetMapping("/page/list")
//    public String listPage(final Model model) {
//        final Flux<City> cityFluxList = cityHandler.findAllCity();
//        model.addAttribute("cityList", cityFluxList);
//        return CITY_LIST_PATH_NAME;
//    }
//
//    @GetMapping("/getByName")
//    public String getByCityName(final Model model,
//                                @RequestParam("cityName") String cityName) {
//        final Mono<City> city = cityHandler.getByCityName(cityName);
//        model.addAttribute("city", city);
//        return CITY_PATH_NAME;
//    }

}
