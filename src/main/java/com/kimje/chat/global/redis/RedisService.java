package com.kimje.chat.global.redis;

import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
	private final RedisTemplate<String, String> redisTemplate;

	public void set(String key, String value, long timeout, TimeUnit timeUnit) {
		log.info("Redis SET 시작: key={}, value={}", key, value);
		redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
		log.info("Redis SET 완료: key={}", key);
	}

	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}

	public boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}
}
