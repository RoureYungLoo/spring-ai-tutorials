package com.luruoyang.record.record;

import java.util.List;

/**
 * @author luruoyang
 */
public record Student(Long id, String name, String className, List<String> hobbies) {
  public void introduce() {
    System.out.println(this);
  }
}
