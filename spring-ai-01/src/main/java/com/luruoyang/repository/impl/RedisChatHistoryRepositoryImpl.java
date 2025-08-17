package com.luruoyang.repository.impl;

import com.luruoyang.enums.BusinessType;
import com.luruoyang.repository.ChatHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
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
    ListOperations<Object, Object> list = redisTemplate.opsForList();
    // 使用LIST保存会话ID
    List<Object> chatIds = list.range(key, 0, -1);
    if (!chatIds.contains(chatId)) {
      list.rightPush(key, chatId);
    }
  }

  @Override
  public List<String> getChatIdsByType(String businessType) {
    String key = "springai:id:" + businessType;
    ListOperations<Object, Object> list = redisTemplate.opsForList();
    // 查询会话ID
    List<Object> chatIds = list.range(key, 0, -1);
    return chatIds.stream().map(Object::toString).collect(Collectors.toList());
  }
}
