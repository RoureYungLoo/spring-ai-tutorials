package com.luruoyang.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luruoyang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
  /**
   * 用户的问题
   */
  private String prompt;
  /**
   * 会话id
   */
  private String chatId;
}