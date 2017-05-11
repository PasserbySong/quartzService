package com.xiejs.aop;

import com.goldcn.common.model.DataResult;
import com.goldcn.common.model.ValidateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Administrator on 2017/5/11.
 */
@ControllerAdvice
public class ValidExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(ValidExceptionHandler.class);

    public ValidExceptionHandler() {
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public DataResult processValidationError(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        DataResult result = new DataResult("001");
        ArrayList list = new ArrayList();
        Iterator i$ = bindingResult.getFieldErrors().iterator();

        while (i$.hasNext()) {
            FieldError fieldError = (FieldError) i$.next();
            ValidateMessage message = new ValidateMessage();
            message.setDefaultMessage(fieldError.getDefaultMessage());
            message.setCode(fieldError.getCode());
            message.setField(fieldError.getField());
            message.setObjectName(fieldError.getObjectName());
            message.setRejectedValue(fieldError.getRejectedValue());
            this.logger.error(fieldError.getField() + "" + fieldError.getDefaultMessage());
            list.add(message);
        }

        result.setData(list);
        return result;
    }
}