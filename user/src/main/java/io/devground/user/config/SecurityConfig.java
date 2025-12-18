package io.devground.user.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import io.devground.auth.jwt.CustomLogoutFilter;
import io.devground.auth.jwt.JWTUtil;
import io.devground.auth.jwt.LoginFilter;
import io.devground.user.service.RedisService;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList(
				"https://dbay.site",
				"https://www.dbay.site",
				"http://localhost:8080",
				"http://localhost:8000",
				"http://localhost:5173"
		));

		configuration.setAllowedMethods(Arrays.asList(
				"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
		));

		configuration.addAllowedHeader("*");
		configuration.addExposedHeader("*");

		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, RedisService redisService) throws Exception {

		return http
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)

				.authorizeHttpRequests(auth -> auth
						// OPTIONS(Preflight) 허용해야 CORS 정상 작동함
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers("/**").permitAll()
						.requestMatchers("/admin").hasRole("ADMIN")
						.requestMatchers("/reissue").permitAll()
						.anyRequest().permitAll()
				)

				// 로그인 / 로그아웃 커스텀 필터
				.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, redisService),
						UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new CustomLogoutFilter(jwtUtil, redisService), LogoutFilter.class)

				// 세션 사용 안함 (JWT stateless)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.build();
	}
}
