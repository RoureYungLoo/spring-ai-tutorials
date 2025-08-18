package com.luruoyang.domain.dto;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author luruoyang
 */
@Data
public class QueryPlanDto {
  // 预算
  @ToolParam(required = false, description = "月租范围（如50元以下、50-100元、100元以上）")
  private double budget;
  // 流量
  @ToolParam(required = false, description = "日均使用场景（轻度文字/视频刷剧/直播）")
  private String data;
  // 通话时长（分钟）
  @ToolParam(required = false, description = "国内通话分钟数、是否需要国际通话")
  private int callMinutes;
  // 附加服务
  @ToolParam(required = false, description = "副卡数量、宽带绑定、合约期接受度")
  private String extra;
}
