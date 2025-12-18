package io.devground.core.util.feign;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.web.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomFeignErrorDecoder implements ErrorDecoder {

	private final ObjectMapper objectMapper;
	private final ErrorDecoder defaultErrorDecoder = new Default();

	@Override
	public Exception decode(String methodKey, Response response) {
		try {
			if (!Objects.isNull(response.body())) {
				InputStream bodyStream = response.body().asInputStream();
				BaseResponse<?> baseResponse = objectMapper.readValue(bodyStream, BaseResponse.class);
				baseResponse.throwIfNotSuccess();
			}

		} catch (ServiceException ex) {
			log.warn("Feign client error - Method: {}, Status: {}, Message: {}",
				methodKey, response.status(), ex.getMessage());
			return ex;

		} catch (IOException ex) {
			log.error("Failed to parse error response body - Method: {}, Status: {}",
				methodKey, response.status(), ex);

		} catch (Exception ex) {
			log.error("Unexpected error in CustomFeignErrorDecoder - Method: {}, Status: {}",
				methodKey, response.status(), ex);
		}

		// 파싱 실패하거나 예상치 못한 에러 발생 시 기본 ErrorDecoder 사용
		return defaultErrorDecoder.decode(methodKey, response);
	}
}
