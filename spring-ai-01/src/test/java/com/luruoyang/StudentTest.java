package com.luruoyang;

import com.luruoyang.record.record.Student;

import java.util.List;

/**
 * @author luruoyang
 */
public class StudentTest {
  public static void main(String[] args) {
    Student student = new Student(1001L, "lisi", "71", List.of("游泳", "唱歌"));
    student.introduce();
  }
}
