package com.dddframework.core.mongodb.contract.exception;

import com.dddframework.core.mongodb.utils.I18nKit;
import com.dddframework.core.mongodb.contract.enums.IEnum;
import com.dddframework.core.mongodb.contract.enums.ResultCode;
import lombok.Data;

/**
 * 服务异常，可用于控制业务异常流程，抛出后由统一异常增强类捕获，返回友好提示
 *
 * @author Jensen
 * @公众号 架构师修行录
 */
@Data
public class ServiceException extends RuntimeException {
    protected Integer code;

    public ServiceException(Integer code, String message) {
        super(I18nKit.get(message));
        this.code = code;
    }

    public ServiceException(Integer code, String message, Object... args) {
        super(I18nKit.get(message, args));
        this.code = code;
    }

    public ServiceException(String message, Object... args) {
        super(I18nKit.get(message, args));
        this.code = ResultCode.FAIL.getCode();
    }

    public ServiceException() {
        this(ResultCode.FAIL.getCode(), ResultCode.FAIL.getDesc());
    }

    public ServiceException(String message) {
        this(ResultCode.FAIL.getCode(), message);
    }

    public ServiceException(Throwable e) {
        this(e.getMessage());
    }

    public ServiceException(IEnum<Integer> resultCode) {
        this(resultCode.getCode(), resultCode.getDesc());
    }

}