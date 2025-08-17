package com.luruoyang.memory;

import ch.qos.logback.core.util.StringUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.luruoyang.domain.Conversation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会话记忆缓存
 *
 * @author luruoyang
 */
@Component
@Slf4j
public class RedisChatMemory implements ChatMemory {
  @Autowired
  private RedisTemplate<Object, Object> redisTemplate;

  /**
   * 缓存会话记忆
   */
  @Override
  public void add(String conversationId, List<Message> messages) {
    ListOperations<Object, Object> cache = redisTemplate.opsForList();

    /* Message -> Conversation */
    messages.forEach(
        msg -> {
          Conversation conversation = Conversation.builder()
              .conversationId(conversationId)
              .message(msg.getText())
              .createTime(LocalDateTime.now())
              .type(msg.getMessageType().getValue())
              .build();
          cache.rightPush("conversation:" + conversationId, JSONUtil.toJsonStr(conversation));
        });

    log.info("缓存会话记忆: {}", conversationId);

  }

  /**
   * 查询会话记忆缓存
   */
  @Override
  public List<Message> get(String conversationId, int lastN) {
    ListOperations<Object, Object> cache = redisTemplate.opsForList();
    List<Object> conversationList = cache.range("conversation:" + conversationId, 0, -1);

    if (CollectionUtils.isEmpty(conversationList)) {
      log.info("conversationList is empty");
      return List.of();
    }
    List<Message> messageList = conversationList.stream().map(item -> {
      Conversation conversation = JSONUtil.toBean(item.toString(), Conversation.class);
      String type = conversation.getType();
      if (StringUtil.isNullOrEmpty(type)) {
        throw new RuntimeException("conversation type can't be null or empty");
      }
      Message message = null;
      switch (MessageType.fromValue(type)) {
        case USER -> message = new UserMessage(conversation.getMessage());
        case ASSISTANT -> message = new AssistantMessage(conversation.getMessage());
      }
      return message;
    }).collect(Collectors.toList());

    log.info("查询会话记忆缓存{}: {}", conversationId, messageList);

    return messageList;
  }

  /**
   * 清除会话记忆
   */
  @Override
  public void clear(String conversationId) {
    redisTemplate.delete("conversation" + conversationId);
  }
}
