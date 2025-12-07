package io.devground.product.infrastructure.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import io.devground.product.infrastructure.util.LogUtils;

@Aspect
@Component
public class ServiceLoggingAspect {

	@Before("@within(org.springframework.stereotype.Service)")
	public void logService(JoinPoint joinPoint) {
		String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
		String methodName = joinPoint.getSignature().getName();

		LogUtils.logServiceRequest(className, methodName);
	}
}
