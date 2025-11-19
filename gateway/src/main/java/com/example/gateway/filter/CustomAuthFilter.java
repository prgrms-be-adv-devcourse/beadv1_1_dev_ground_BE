package com.example.gateway.filter;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.devground.core.model.web.BaseResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Flux;

import io.jsonwebtoken.JwtException;

@Component
public class CustomAuthFilter extends AbstractGatewayFilterFactory<CustomAuthFilter.Config> {

	@Value("${custom.jwt.secrets.app-key}")
	private String secretKey;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final ObjectMapper om = new ObjectMapper();

	public static class Config {
	}

	public CustomAuthFilter() {

		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {

		return (exchange, chain) -> {
			log.info("요청이 필터에 도착함: {}", exchange.getRequest().getURI());

			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();

			if (!request.getHeaders().containsKey("access") && !request.getCookies()
				.containsKey("refresh")) {

				log.error("토큰 혹은 쿠키가 비어있습니다!");

				return chain.filter(exchange);
			}

			Optional<String> tokenOptional = resolveToken(request);

			if (tokenOptional.isEmpty()) {
				// 토큰 없으면 에러 처리 없이 그냥 다음 필터로 넘김 (비로그인 상태 허용)
				return chain.filter(exchange);
			}

			String token = tokenOptional.get();

			if (!isValidToken(token)) {
				return response.writeWith(
					Flux.just(
						writeUnauthorizedResponseBody(response)
					)
				);
			}

			Jws<Claims> claims = getClaims(token);

			String userCode = claims.getPayload().get("userCode").toString();
			String role = claims.getPayload().get("role").toString();

			ServerHttpRequest mutatedRequest = request.mutate()
				.header("X-CODE", userCode)
				.header("ROLE", role)
				.build();

			return chain.filter(exchange.mutate().request(mutatedRequest).build());

		};
	}

	private DataBuffer writeUnauthorizedResponseBody(ServerHttpResponse response) {
		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		response.getHeaders().add(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/json");

		BaseResponse<Object> body = BaseResponse.success(401, "인증이 필요합니다!");

		return response.bufferFactory().wrap(writeResponseBody(body));
	}

	private byte[] writeResponseBody(BaseResponse<Object> body) {
		try {
			return om.writeValueAsBytes(body);
		} catch (JsonProcessingException e) {
			log.error("Serialization 오류");
			throw new RuntimeException(e);
		}
	}

	private Optional<String> resolveToken(ServerHttpRequest request) {

		String token = request.getHeaders().getFirst("access");

		if (token != null && !token.isBlank()) {
			// "Bearer "로 시작하면 잘라내고, 아니면 그대로 사용
			if (token.startsWith("Bearer ")) {
				return Optional.of(token.substring(7));
			}
			return Optional.of(token);
		}

		return Optional.empty();
	}

	private boolean isValidToken(String token) {

		try {
			getClaims(token);
			return true;
		} catch (JwtException e) {
			log.info("Invalid JWT Token was detected: {}  msg : {}", token, e.getMessage());
		} catch (IllegalArgumentException e) {
			log.info("JWT claims String is empty: {}  msg : {}", token, e.getMessage());
		} catch (Exception e) {
			log.error("an error raised from validating token : {}  msg : {}", token, e.getMessage());
		}

		return false;
	}

	private Jws<Claims> getClaims(String token) {
		return Jwts.parser()
			.verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
			.build()
			.parseSignedClaims(token);
	}
}
