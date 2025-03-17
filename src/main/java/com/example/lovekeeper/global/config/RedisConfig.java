package com.example.lovekeeper.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.lettuce.core.ReadFrom;

@Configuration
public class RedisConfig {

	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		// Redis 단일 인스턴스 설정
		RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
		redisConfig.setHostName("lovekeeper-redis"); // Docker 컨테이너 이름
		redisConfig.setPort(6379);                   // Docker 매핑 포트

		// Lettuce 클라이언트 설정: 마스터 노드에서만 읽기/쓰기
		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
			.readFrom(ReadFrom.MASTER)           // 마스터에서만 동작하도록 강제
			.build();

		return new LettuceConnectionFactory(redisConfig, clientConfig);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		// 키와 값을 직렬화 방식 설정 (문자열로 처리)
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		template.afterPropertiesSet();
		return template;
	}
}