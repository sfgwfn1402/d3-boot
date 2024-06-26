package com.dddframework.demo.application.listener;

import com.dddframework.core.utils.BizAssert;
import com.dddframework.demo.domain.contract.event.UserRegisterEvent;
import com.dddframework.demo.domain.erp.model.ErpR;
import com.dddframework.demo.domain.erp.model.ErpUser;
import com.dddframework.demo.domain.erp.service.ErpFeignService;
import com.dddframework.demo.domain.user.model.UserModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * ERP服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ErpListener {
    final ErpFeignService erpFeignService;

    /**
     * 在ERP注册用户（异步）
     *
     * @param event
     */
    @Async
    @EventListener
    public void register(UserRegisterEvent event) {
        UserModel user = event.get();
        ErpR<ErpUser> erpR = erpFeignService.register(ErpUser.builder().extId(user.getId()).gender(Integer.valueOf(user.getSex())).age(user.getAge()).name(user.getRealName()).build());
        BizAssert.isOk(erpR, "ERP用户注册失败：{}", erpR);
    }

}
