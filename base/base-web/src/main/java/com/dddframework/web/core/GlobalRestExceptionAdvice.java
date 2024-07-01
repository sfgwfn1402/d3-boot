package com.dddframework.web.core;

import com.dddframework.core.context.ThreadContext;
import com.dddframework.core.contract.R;
import com.dddframework.core.contract.constant.ContextConstants;
import com.dddframework.core.contract.enums.ResultCode;
import com.dddframework.core.contract.exception.ServiceException;
import com.dddframework.core.contract.exception.ValidateException;
import com.dddframework.core.utils.ExceptionKit;
import com.dddframework.core.utils.JsonKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalRestExceptionAdvice {
    private static final Logger log = LoggerFactory.getLogger("### BASE-WEB : GlobalRestExceptionAdvice ###");

    @ExceptionHandler({BindException.class})
    public R<String> bindException(HttpServletRequest request, Model model, BindException e) {
        String projectStackTrace = ExceptionKit.getProjectStackTraces(e);
        ThreadContext.set(ContextConstants.SEE, projectStackTrace);
        List<String> errList = e.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        log.error("请求参数校验失败：{} {}", errList, model);
        return R.fail(ResultCode.PARAMETER_VALIDATION_FAILED.getCode(), String.join(",", errList), "@See " + projectStackTrace);
    }

    @ExceptionHandler({ValidateException.class})
    public R<String> validatorException(HttpServletRequest request, ValidateException e) {
        String projectStackTrace = ExceptionKit.getProjectStackTraces(e);
        ThreadContext.set(ContextConstants.SEE, projectStackTrace);
        log.error("请求参数校验失败：{}\n**StackTraces:** {}", e.getMessage(), projectStackTrace);
        return R.fail(ResultCode.PARAMETER_VALIDATION_FAILED.getCode(), e.getMessage(), "@See " + projectStackTrace);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public R<String> methodArgumentNotValidExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String projectStackTrace = ExceptionKit.getProjectStackTraces(e);
        ThreadContext.set(ContextConstants.SEE, projectStackTrace);
        log.error("请求参数校验失败：{}\n**StackTraces:** {}", fieldError.getDefaultMessage(), projectStackTrace);
        e.printStackTrace();
        return R.fail(ResultCode.PARAMETER_VALIDATION_FAILED.getCode(), fieldError.getDefaultMessage(), "@See " + projectStackTrace);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<String> duplicate(HttpServletRequest req, DuplicateKeyException e) {
        log.error("", e);
        return R.fail(ResultCode.BAD_REQUEST.getCode(), "重复提交数据！", e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<String> handle(HttpServletRequest request, NoHandlerFoundException e) {
        log.error("", e);
        return R.fail(404, "地址错误！！！" + request.getRequestURI() + "非法访问!");
    }

    @ExceptionHandler({ServiceException.class})
    public R<Map<String, String>> serviceException(HttpServletRequest request, ServiceException e) {
        String projectStackTrace = ExceptionKit.getProjectStackTraces(e);
        log.warn("服务异常：{}\nContext：{}\n@See {}", e.getMessage(), JsonKit.toJson(ThreadContext.getValues()), projectStackTrace);
        Map<String, String> data = new HashMap<>();
        data.put("context", JsonKit.toJson(ThreadContext.getValues()));
        data.put("stackTraces", projectStackTrace);
        return R.fail(e.getCode(), e.getMessage(), data);
    }

    @ExceptionHandler({RuntimeException.class})
    public R<String> runTimeException(HttpServletRequest request, RuntimeException e) {
        String projectStackTrace = ExceptionKit.getProjectStackTraces(e);
        ThreadContext.set(ContextConstants.SEE, projectStackTrace);
        log.error("运行时异常：{}\n**StackTraces:** {}", e.getMessage(), projectStackTrace);
        e.printStackTrace();
        return R.fail(ResultCode.FAIL.getCode(), e.getMessage(), "@See " + projectStackTrace);
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public R<String> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e) {
        String projectStackTrace = ExceptionKit.getProjectStackTraces(e);
        log.error("请求超时：{}\n**StackTraces:** {}", e.getMessage(), projectStackTrace);
        e.printStackTrace();
        return R.fail(HttpStatus.REQUEST_TIMEOUT.value(), e.getMessage(), "请求超时");
    }

}
