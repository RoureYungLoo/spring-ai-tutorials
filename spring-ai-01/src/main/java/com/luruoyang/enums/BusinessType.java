package com.luruoyang.enums;

import lombok.Getter;

/**
 * 业务类型
 *
 * @author luruoyang
 */
@Getter
public enum BusinessType {
  CHAT("chat"),
  GAME("game"),
  SERVICE("service"),
  PDF("pdf");

  private final String value;

  BusinessType(String value) {
    this.value = value;
  }

  public static BusinessType fromValue(String value) {
    for (BusinessType businessType : BusinessType.values()) {
      if (businessType.getValue().equals(value)) {
        return businessType;
      }
    }
    throw new IllegalArgumentException("Invalid BusinessType value: " + value);
  }
}

