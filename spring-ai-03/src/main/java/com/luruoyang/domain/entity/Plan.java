package com.luruoyang.domain.entity;

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
// 实体类
public class Plan {
  // 唯一标识符
  private Long id;
  // 套餐名称
  private String name;
  // 月租（元）
  private double monthlyRent;
  // 流量
  private String data;
  // 通话时长（分钟）
  private int callMinutes;
  // 附加服务
  private String extraServices;
  // 合约期（月）
  private int contractPeriod;
  // 优惠活动
  private String promotion;
}