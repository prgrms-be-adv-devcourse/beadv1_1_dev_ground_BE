package io.devground.product.infrastructure.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import io.devground.product.infrastructure.util.LogUtils;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class GlobalExceptionLoggingAspect {

	@AfterThrowing(pointcut = "@within(org.springframework.stereotype.Service)", throwing = "ex")
	public void logGlobalException(JoinPoint joinPoint, Throwable ex) {
		LogUtils.logError(joinPoint, ex);
	}
}
