package com.luruoyang.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author luruoyang
 */
@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<Object, Object> template = new RedisTemplate<>();

    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

    template.setConnectionFactory(factory);
    template.afterPropertiesSet();

    return template;
  }
}
