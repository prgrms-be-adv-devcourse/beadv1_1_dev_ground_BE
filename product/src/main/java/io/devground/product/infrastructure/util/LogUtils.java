package io.devground.product.infrastructure.util;

import org.aspectj.lang.JoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.core.model.web.BaseResponse;
import io.devground.product.domain.exception.DomainException;
import io.devground.product.domain.vo.DomainErrorCode;
import io.devground.product.infrastructure.app.AppConfig;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j(topic = "service")
public class LogUtils {

	public void logControllerRequest(String className, String methodName) {

		log.info("Request Controller = [{}.{}]", className, methodName);
	}

	public void logControllerResponse(
		String className, String methodName, BaseResponse<?> baseResponse
	) throws Throwable {

		String jsonData = AppConfig.getObjectMapper().writeValueAsString(baseResponse.data());

		log.info("Response Controller = [{}.{}], status: [{}], message: [{}], data: [{}]",
			className, methodName, baseResponse.resultCode(), baseResponse.msg(), jsonData
		);
	}

	public void logServiceRequest(String className, String methodName) {

		log.info("Request Service = [{}.{}]", className, methodName);
	}

	public void logError(JoinPoint joinPoint, Throwable ex) {
		String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		String status;
		String msg;

		switch (ex) {
			case DomainException exception -> {
				DomainErrorCode errorCode = exception.getErrorCode();

				status = String.valueOf(errorCode.getHttpStatus());
				msg = errorCode.getMessage();
			}
			case ServiceException exception -> {
				ErrorCode errorCode = exception.getErrorCode();

				status = String.valueOf(errorCode.getHttpStatus());
				msg = errorCode.getMessage();
			}
			case MethodArgumentNotValidException exception -> {
				status = HttpStatus.BAD_REQUEST.toString();
				msg = exception.getMessage();
			}
			default -> {
				status = "UNKNOWN";
				msg = ex.getMessage();
			}
		}

		log.error("ERROR = [{}.{}], status: [{}], message: [{}]",
			className, methodName, status, msg
		);
	}
}
