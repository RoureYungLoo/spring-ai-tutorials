package com.luruoyang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author luruoyang
 */
@MapperScan("com.luruoyang.mapper")
@SpringBootApplication
public class SpringAi01Application {

  public static void main(String[] args) {
    SpringApplication.run(SpringAi01Application.class, args);
  }

}
