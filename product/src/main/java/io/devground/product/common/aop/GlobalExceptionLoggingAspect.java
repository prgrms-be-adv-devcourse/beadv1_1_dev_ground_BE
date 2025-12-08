package io.devground.product.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import io.devground.product.common.util.LogUtils;

@Aspect
@Component
public class GlobalExceptionLoggingAspect {

	@AfterThrowing(pointcut = "@within(org.springframework.stereotype.Service)", throwing = "ex")
	public void logGlobalException(JoinPoint joinPoint, Throwable ex) {
		LogUtils.logError(joinPoint, ex);
	}
}
