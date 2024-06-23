package com.dddframework.demo.domain.erp.model;

import com.dddframework.core.contract.IR;
import lombok.Data;

/**
 * ERP公共响应
 *
 * @author Jensen
 */
@Data
public class ErpR<T> implements IR {
    public String code;
    public String msg;
    public T data;

    public Boolean isOk() {
        return code.equals("0000");
    }
}