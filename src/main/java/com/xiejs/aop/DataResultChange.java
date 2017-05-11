package com.xiejs.aop;


import com.goldcn.common.exception.ServiceException;
import com.goldcn.common.model.DataResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;


/**
 * Created by Administrator on 2017/5/11.
 */
@Aspect
@Component
public class DataResultChange {
    private Logger logger = LoggerFactory.getLogger(DataResultChange.class);

    public DataResultChange() {
    }

    @Around("execution(*  com.xiejs.*..*Controller.*(..))")
    public Object logServiceAccess(ProceedingJoinPoint pjp) {
        String className = pjp.getTarget().getClass().getName();
        DataResult result = null;

        try {
            Object result1 = pjp.proceed();
            result = new DataResult(result1);
        } catch (Throwable var5) {
            this.logger.error(var5.getMessage(), var5);
            if(var5 instanceof DataAccessException) {
                result = new DataResult("003");
            } else if(var5 instanceof ServiceException) {
                result = new DataResult(((ServiceException)var5).getErrorCode(), var5.getLocalizedMessage());
            } else if(var5 instanceof MethodArgumentNotValidException) {
                result = new DataResult("002", var5.getLocalizedMessage());
            } else {
                result = new DataResult("000");
            }
        }

        return result;
    }
}
