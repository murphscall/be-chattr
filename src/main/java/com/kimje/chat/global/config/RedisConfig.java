package com.kimje.chat.global.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableCaching
public class RedisConfig {

	private final ObjectMapper objectMapper;

	RedisConfig(ObjectMapper objectMapper){
		this.objectMapper  = objectMapper;
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, String> template = new RedisTemplate<String, String>(); // ✅ 제네릭 명시
		template.setConnectionFactory(factory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new StringRedisSerializer());

		return template;
	}

	@Bean
	@Primary
	public CacheManager cacheManager(RedisConnectionFactory factory) {

		PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator
			.builder()
			.allowIfSubType("com.kimje.chat")
			.allowIfSubType("java.util")
			.build();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.activateDefaultTyping(typeValidator , ObjectMapper.DefaultTyping.NON_FINAL);


		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
			.disableCachingNullValues()
			.entryTtl(Duration.ofMinutes(15))
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeKeysWith(
				RedisSerializationContext.SerializationPair.fromSerializer(
					new StringRedisSerializer())
			)
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(
					new GenericJackson2JsonRedisSerializer(objectMapper)
				)
			);

		return RedisCacheManager
			.RedisCacheManagerBuilder
			.fromConnectionFactory(factory)
			.cacheDefaults(configuration)
			.build();
	}
}
