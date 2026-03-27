package com.luruoyang.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.ai.chat.messages.AssistantMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author luruoyang
 */
@Builder
@Data
@TableName("conversation")
public class Conversation {
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;
  private String conversationId;
  private String message;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;
  private String type;
}
