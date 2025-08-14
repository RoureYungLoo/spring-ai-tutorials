package com.luruoyang.memory;

import ch.qos.logback.core.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luruoyang.domain.Conversation;
import com.luruoyang.mapper.ConversationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会话记忆持久化
 *
 * @author luruoyang
 */
@Component
@Slf4j
public class JdbcChatMemory implements ChatMemory {

  @Autowired
  private ConversationMapper conversationMapper;

  /**
   * 保存会话记忆
   */
  @Override
  public void add(String conversationId, List<Message> messages) {
    /* Message -> Conversation */
    List<Conversation> list = messages.stream().map(msg -> Conversation.builder()
        .conversationId(conversationId)
        .message(msg.getText())
        .createTime(LocalDateTime.now())
        .type(msg.getMessageType().getValue())
        .build()).collect(Collectors.toList());
    conversationMapper.insert(list);

    log.info("保存会话记忆: {}", list);

  }

  /**
   * 读取会话记忆
   */
  @Override
  public List<Message> get(String conversationId, int lastN) {
    /* Conversation -> Message */
    IPage<Conversation> page = new Page<>(1, lastN);
    LambdaQueryWrapper<Conversation> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(!StringUtil.isNullOrEmpty(conversationId), Conversation::getConversationId, conversationId);
    page = conversationMapper.selectPage(page, queryWrapper);
    List<Conversation> conversationList = page.getRecords();
    if (CollectionUtils.isEmpty(conversationList)) {
      log.info("conversationList is empty");
      return List.of();
    }

    List<Message> messageList = conversationList.stream().map(conversation -> {
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

    log.info("读取会话记忆{}: {}", conversationId, messageList);

    return messageList;
  }

  /**
   * 清除会话记忆
   */
  @Override
  public void clear(String conversationId) {
    conversationMapper.deleteById(conversationId);
    log.info("清除会话记忆: {}", conversationId);
  }

}
