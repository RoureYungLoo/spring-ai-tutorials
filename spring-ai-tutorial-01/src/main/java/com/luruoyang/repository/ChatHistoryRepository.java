package com.luruoyang.repository;

import com.luruoyang.enums.BusinessType;

import java.util.List;

/**
 * 会话历史记录
 *
 * @author luruoyang
 */

public interface ChatHistoryRepository {

  public void saveHistory(BusinessType businessType, String chatId);

  List<String> getChatIdsByType(String businessType);
}
