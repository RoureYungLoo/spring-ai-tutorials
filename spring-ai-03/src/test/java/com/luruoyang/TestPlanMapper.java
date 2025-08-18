package com.luruoyang;

import com.luruoyang.domain.entity.Plan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author luruoyang
 */
@SpringBootTest
public class TestPlanMapper {

  @Autowired
  private PlanMapper planMapper;

  @Test
  public void test() {
    planMapper.insert(new Plan(null, "经济卡", 39.0, "10GB", 100, "无", 0, "首月半价"));
    planMapper.insert(new Plan(null, "畅享全家享", 99.0, "50GB", 500, "200M宽带+2张副卡", 12, "送视频会员月卡"));
    planMapper.insert(new Plan(null, "全球通尊享", 199.0, "不限量", 2000, "国际漫游+5G优先", 24, "机场贵宾厅2次/年"));
    planMapper.insert(new Plan(null, "学生青春卡", 59.0, "30GB", 200, "校园网加速", 6, "免流特定APP"));
  }
}
