package com.sample.web.profile;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@c24.incept5.com
 * @since 13/11/2012
 */
@Aspect
public class ResourceProfiler {

    Logger LOG = LoggerFactory.getLogger(ResourceProfiler.class);

    @Pointcut("execution(* com.sample.web.resource.*.*(..))")
    public void businessMethods() {
    }

    @Around("businessMethods()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
//        LOG.debug("Profiling method {} start", method.getName());
        Object output = pjp.proceed();
//        LOG.debug("Profiling method {} end", method.getName());
        long elapsedTime = System.currentTimeMillis() - start;
        LOG.debug("Profiling method {} : execution time: {} milliseconds.", method.getName(), elapsedTime);
        return output;
    }
}
