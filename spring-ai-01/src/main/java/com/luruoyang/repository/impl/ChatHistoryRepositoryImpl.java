package com.luruoyang.repository.impl;

import com.luruoyang.enums.BusinessType;
import com.luruoyang.repository.ChatHistoryRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存, 保存会话ID
 * @author luruoyang
 */
//@Component
public class ChatHistoryRepositoryImpl implements ChatHistoryRepository {

  Map<BusinessType, List<String>> chatHistoryMap = new ConcurrentHashMap<>();

  @Override
  public void saveHistory(BusinessType businessType, String chatId) {
    /* 该Type类型的第一个会话 */
    chatHistoryMap.putIfAbsent(businessType, new ArrayList<>());
    /* 该Type类型的后续会话 */
    chatHistoryMap.get(businessType).add(chatId);
  }

  @Override
  public List<String> getChatIdsByType(String businessType) {
    BusinessType type = BusinessType.fromValue(businessType);
    return chatHistoryMap.get(type);
  }
}
