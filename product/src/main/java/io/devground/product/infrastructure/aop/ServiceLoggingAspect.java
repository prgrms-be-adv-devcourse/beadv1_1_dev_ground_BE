package io.devground.product.infrastructure.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import io.devground.product.infrastructure.util.LogUtil;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class ServiceLoggingAspect {

	@Before("@within(org.springframework.stereotype.Service)")
	public Object logService(JoinPoint joinPoint) {
		String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
		String methodName = joinPoint.getSignature().getName();

		LogUtil.logServiceRequest(className, methodName);

		return joinPoint;
	}
}
