package com.dddframework.data.mongodb.demo;

import com.dddframework.data.mongodb.repository.impl.MongoBaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例
 */
@RestController
@RequestMapping("/demo")
public class CrudController {


    @Autowired
    private MongoBaseRepositoryImpl repository;



}
