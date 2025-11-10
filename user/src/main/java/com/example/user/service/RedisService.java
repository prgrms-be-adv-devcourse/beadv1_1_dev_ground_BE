package com.example.user.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisService {
	private final RedisTemplate<String, Object> template;
	private final StringRedisTemplate stringRedisTemplate;
	private final ObjectMapper objectMapper;

	public void save(String key, String value, Duration timeout) {
		if (value instanceof String str) {
			stringRedisTemplate.opsForValue().set(key, str, timeout);
		} else {
			template.opsForValue().set(key, value, timeout);
		}
	}

	public <T> T find(String key, Class<T> clazz) {
		if (clazz == String.class) {
			String value = stringRedisTemplate.opsForValue().get(key);
			return clazz.cast(value);
		}
		Object rawData = stringRedisTemplate.opsForValue().get(key);
		if (rawData == null) {
			return null;
		}

		return objectMapper.convertValue(rawData, clazz);
	}

	public void delete(String key) {
		template.delete(key);
	}
}