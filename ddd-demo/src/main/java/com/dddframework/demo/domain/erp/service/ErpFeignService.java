package com.dddframework.demo.domain.erp.service;

import com.dddframework.demo.domain.erp.model.ErpR;
import com.dddframework.demo.domain.erp.model.ErpUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 金华佗Feign
 *
 * @author Jensen
 */
@FeignClient(value = "erp", url = "http://xxx.com/erp-api")
public interface ErpFeignService {

    /**
     * ERP用户注册
     *
     * @param erpUser
     * @return
     */
    @PostMapping("/user/register")
    ErpR<ErpUser> register(@SpringQueryMap ErpUser erpUser);
}
