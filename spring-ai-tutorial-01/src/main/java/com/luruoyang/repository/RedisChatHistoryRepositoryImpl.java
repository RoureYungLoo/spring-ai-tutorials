package com.luruoyang.repository;

import com.luruoyang.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于Redis, 保存会话ID
 *
 * @author luruoyang
 */
@Component
public class RedisChatHistoryRepositoryImpl implements ChatHistoryRepository {

  @Autowired
  private RedisTemplate<Object, Object> redisTemplate;

  @Override
  public void saveHistory(BusinessType businessType, String chatId) {
    String key = "springai:id:" + businessType.getValue();
    SetOperations<Object, Object> set = redisTemplate.opsForSet();
    // 使用SET保存会话ID
    set.add(key, chatId);
  }

  @Override
  public List<String> getChatIdsByType(String businessType) {
    String key = "springai:id:" + businessType;
    SetOperations<Object, Object> set = redisTemplate.opsForSet();
    // 查询会话ID
    Set<Object> chatIds = set.members(key);
    return chatIds.stream().map(Object::toString).collect(Collectors.toList());
  }
}
