package io.devground.payments.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import io.devground.payments.common.util.LogUtil;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class GlobalExceptionLoggingAspect {

	@AfterThrowing(pointcut = "@within(org.springframework.stereotype.Service)", throwing = "ex")
	public void logGlobalException(JoinPoint joinPoint, Throwable ex) {
		LogUtil.logError(joinPoint, ex);
	}
}
