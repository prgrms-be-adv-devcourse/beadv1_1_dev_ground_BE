package io.devground.payments.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import io.devground.core.model.web.BaseResponse;
import io.devground.payments.common.util.LogUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class BaseResponseAspect {

	private final HttpServletResponse response;

	@Around("within(@org.springframework.web.bind.annotation.RestController *)")
	public Object updateHttpStatus(ProceedingJoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
		String methodName = joinPoint.getSignature().getName();

		LogUtil.logControllerRequest(className, methodName);

		Object proceed = joinPoint.proceed();

		if (proceed instanceof BaseResponse<?> baseResponse) {
			LogUtil.logControllerResponse(className, methodName, baseResponse);

			response.setStatus(baseResponse.resultCode());
		}

		return proceed;
	}
}
