package io.devground.dbay.order.common.aop;

import io.devground.core.model.web.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class BaseResponseAspect {

	private final HttpServletResponse response;

	@Around("within(@org.springframework.web.bind.annotation.RestController *)")
	public Object updateHttpStatus(ProceedingJoinPoint joinPoint) throws Throwable {
		Object proceed = joinPoint.proceed();

		if (proceed instanceof BaseResponse<?> baseResponse) {
			response.setStatus(baseResponse.resultCode());
		}

		return proceed;
	}
}
