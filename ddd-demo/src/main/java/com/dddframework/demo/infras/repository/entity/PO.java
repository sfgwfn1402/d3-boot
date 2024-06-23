package com.dddframework.demo.infras.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.dddframework.data.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@OrderBy("create_time")
public class PO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    @TenantId
    private String tenantId;
    @SystemId
    private String systemId;
    @OnCreate
    private LocalDateTime createTime;
    @OnCreate
    @OnUpdate
    private LocalDateTime updateTime;
    @TableLogic
    private Boolean delFlag;
}
