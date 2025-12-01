package io.devground.user.service;

import java.time.Duration;
import java.util.Optional;

import javax.management.relation.Role;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.devground.auth.jwt.JWTUtil;
import io.devground.core.events.user.UserCreatedEvent;
import io.devground.core.model.entity.RoleType;
import io.devground.core.model.vo.ErrorCode;
import io.devground.user.mapper.UserMapper;
import io.devground.user.model.dto.request.KakaoCodeRequest;
import io.devground.user.model.dto.request.KakaoUserRequest;
import io.devground.user.model.dto.response.LoginResponse;
import io.devground.user.model.entity.User;
import io.devground.user.repository.UserRepository;
import io.devground.user.utils.RandomNickname;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoServiceImpl implements KakaoService {

	private final UserRepository userRepository;
	@Value("${kakao.api-key}")
	private String apiKey;

	@Value("${kakao.redirect-uri}")
	private String redirectUri;

	private final RedisService redisService;
	private final JWTUtil jwtUtil;
	private final RandomNickname randomNickname;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${users.events.topic.join}")
	private String userJoinEventsTopicName;


	@Override
	public LoginResponse kakaoLogin(KakaoCodeRequest kakaoCodeRequest, HttpServletResponse response,
		HttpServletRequest request) {
		String accessToken = getKakaoAccessToken(kakaoCodeRequest.code());
		KakaoUserRequest kakaoUser = getKakaoUser(accessToken);

		Optional<User> user = userRepository.findByOauthId(kakaoUser.oauthId());

		if (user.isEmpty()) {
			registerKakaoUser(kakaoUser);
			user = Optional.of(userRepository.findByEmail(kakaoUser.email()));
		}

		login(user.get(), response, request);
		RoleType role = user.get().getRole();
		return new LoginResponse(role.getText());
	}

	private String getKakaoAccessToken(String code) {
		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", apiKey);                // 🔑 카카오 REST API 키
		params.add("redirect_uri", redirectUri);        // 🔄 등록된 redirect_uri
		params.add("code", code);                       // 📝 받은 인가 코드

		HttpEntity<MultiValueMap<String, String>> kakaoTokenReq = new HttpEntity<>(params, headers);
		ResponseEntity<String> res = null;
		try {
			res = rt.exchange(
				"https://kauth.kakao.com/oauth/token",
				HttpMethod.POST,
				kakaoTokenReq,
				String.class
			);
		} catch (HttpClientErrorException e) {
			log.error("[kakao Login HTTP API 오류] {}", e.getMessage());
			throw ErrorCode.INVALID_TOKEN.throwServiceException();
		}

		// HTTP 응답 (JSON) -> Access Token 파씽
		String resBody = res.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = null;
		try {
			jsonNode = objectMapper.readTree(resBody);
		} catch (JsonProcessingException e) {
			log.error("[json 파싱 오류] {}", e.getMessage());
		}

		// Access Token 반환
		return jsonNode.get("access_token").asText();
	}

	//카카오 회원 정보가 있을 시 로그인
	private String login(User user, HttpServletResponse response, HttpServletRequest request) {
		String email = user.getEmail();
		String userCode = user.getCode();
		String role = RoleType.USER.getText();

		String login = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refresh")) {
					login = cookie.getValue();
				}
			}

			if (login != null) {
				redisService.delete(login);
			}
		}

		//토큰 생성
		String access = jwtUtil.createJwt("access", userCode, role, 3600000L); //1시간
		String refresh = jwtUtil.createJwt("refresh", userCode, role, 604800000L); //7일

		//refresh 토큰 저장
		redisService.save(refresh, email, Duration.ofDays(7));

		//응답 설정
		response.setHeader("access", access);
		response.addCookie(createCookie("refresh", refresh));
		response.setStatus(HttpStatus.OK.value());

		return "로그인";
	}

	private KakaoUserRequest getKakaoUser(String accessToken) {
		// HTTP Header 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// User 정보 HTTP 요청 보내기
		HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
		RestTemplate rt = new RestTemplate();
		ResponseEntity<String> res = null;
		try {
			res = rt.exchange(
				"https://kapi.kakao.com/v2/user/me",
				HttpMethod.POST,
				kakaoUserInfoRequest,
				String.class
			);
		} catch (HttpClientErrorException e){
			log.error("[kakao Data Access API 오류] {}", e.getMessage());
			throw ErrorCode.KAKAO_DATA_ACCESS_API.throwServiceException();
		}

		String resBody = res.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = null;
		try {
			jsonNode = objectMapper.readTree(resBody);
		} catch (JsonProcessingException e) {
			log.error("[json 파싱 오류] {}", e.getMessage());
			throw ErrorCode.JSON_PARSING_ERROR.throwServiceException();
		}

		// 필요한 값 json에서 파싱
		//닉네임을 따로 입력하지 않아 랜덤으로 지정
		String nickname = randomNickname.generate();
		log.info("nickname = {}", nickname);
		String email = jsonNode.path("kakao_account").path("email").asText();
		String name = jsonNode.path("kakao_account").path("name").asText();
		String phone = jsonNode.path("kakao_account").path("phone_number").asText();
		phone = phone.replace("+82 10-", "010-");
		log.info("phone = {}", phone);
		String address = jsonNode.path("kakao_account").path("shipping_address").path("base_address").asText();
		String addressee = jsonNode.path("kakao_account").path("shipping_address").path("detail_address").asText();
		Long oauthId = jsonNode.get("id").asLong();
		//카카오 유저는 비밀번호가 없어서 랜덤 값으로 지정
		String password = RandomStringUtils.randomAlphanumeric(10);

		KakaoUserRequest kakaoUser = new KakaoUserRequest(name, email, password, nickname, oauthId, phone, address, addressee);
		return kakaoUser;
	}

	private void registerKakaoUser(KakaoUserRequest kakaoUserRequest) {
		String email = kakaoUserRequest.email();
		Long oauthId = kakaoUserRequest.oauthId();

		Boolean isExist = userRepository.existsByEmail(email);

		if(isExist){
			User user = userRepository.findByEmail(email);
			user.setOauthId(oauthId);
			userRepository.save(user);

			return;
		}

		User user = UserMapper.kakaoToEntity(kakaoUserRequest, oauthId,  bCryptPasswordEncoder);
		String userCode = userRepository.save(user).getCode();
		UserCreatedEvent event = new UserCreatedEvent(userCode);
		log.info("Sending event: {}", event);
		kafkaTemplate.send(userJoinEventsTopicName, event.userCode(), event);
	}

	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24*60*60*7);
		cookie.setHttpOnly(true);
		cookie.setDomain("");
		cookie.setSecure(true);
		cookie.setAttribute("SameSite", "None");
		cookie.setPath("/");

		return cookie;
	}

}
