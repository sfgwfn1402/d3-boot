package com.dddframework.demo.infras.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.dddframework.data.elasticsearch.annotation.SystemId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PO {
    // 索引名称
    public String index;
}
