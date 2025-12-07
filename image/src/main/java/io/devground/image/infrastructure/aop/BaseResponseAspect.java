package io.devground.image.infrastructure.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import io.devground.core.model.web.BaseResponse;
import io.devground.image.infrastructure.util.LogUtils;
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

		LogUtils.logControllerRequest(className, methodName);

		Object proceed = joinPoint.proceed();

		if (proceed instanceof BaseResponse<?> baseResponse) {
			LogUtils.logControllerResponse(className, methodName, baseResponse);

			response.setStatus(baseResponse.resultCode());
		}

		return proceed;
	}
}
