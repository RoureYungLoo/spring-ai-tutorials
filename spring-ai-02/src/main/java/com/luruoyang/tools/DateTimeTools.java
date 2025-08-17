package com.luruoyang.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author luruoyang
 */
@Component
public class DateTimeTools {

  @Tool(description = "获取当前时间")
  public String getDateTime() {
    return LocalDateTime.now().toString();
  }

  @Tool(description = "设置给定时间的闹钟")
  public String setAlarm(@ToolParam(required = true, description = "闹钟时间") String time) {
    LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
    String res = "Alarm set for " + alarmTime;
    System.out.println("=========================" + res);
    return res;

  }
}
