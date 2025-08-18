package com.luruoyang.repository;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.luruoyang.PlanMapper;
import com.luruoyang.domain.dto.QueryPlanDto;
import com.luruoyang.domain.entity.Plan;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据访问层
 *
 * @author lenovo
 */
@Repository
public class PlanRepository {
  @Autowired
  private PlanMapper planMapper;

  // 使用线程安全的List存储数据
  private final List<Plan> plans = new CopyOnWriteArrayList<>();
  // ID生成器
  private final AtomicLong idGenerator = new AtomicLong(1);

  // 添加套餐
  public void addPlan(Plan plan) {
    plan.setId(idGenerator.getAndIncrement());
    plans.add(plan);
  }

  // 删除套餐
  public boolean deletePlan(Long id) {
    return plans.removeIf(p -> p.getId().equals(id));
  }

  // 更新套餐
  public Plan updatePlan(Plan updatedPlan) {
    for (Plan plan : plans) {
      if (plan.getId().equals(updatedPlan.getId())) {
        plan.setName(updatedPlan.getName());
        plan.setMonthlyRent(updatedPlan.getMonthlyRent());
        plan.setData(updatedPlan.getData());
        plan.setCallMinutes(updatedPlan.getCallMinutes());
        plan.setExtraServices(updatedPlan.getExtraServices());
        plan.setContractPeriod(updatedPlan.getContractPeriod());
        plan.setPromotion(updatedPlan.getPromotion());
        return plan;
      }
    }
    // 未找到对应ID的套餐
    return null;
  }

  // 查询所有套餐
  public List<Plan> findAllPlans() {
    // 返回副本保证数据安全
    return new ArrayList<>(plans);
  }

  // 按ID查询套餐
  public Optional<Plan> findPlanById(Long id) {
    return plans.stream()
        .filter(p -> p.getId().equals(id))
        .findFirst();
  }

  // 按名称查询套餐
  public List<Plan> findPlansByName(String name) {
    return plans.stream()
        .filter(p -> p.getName().contains(name))
        .toList();
  }

  public List<Plan> queryPlans(QueryPlanDto queryPlanDto) {
    // 预算
    double budget = queryPlanDto.getBudget();
    // 通话
    int callMinutes = queryPlanDto.getCallMinutes();
    // 流量
    String data = queryPlanDto.getData();
    // 额外服务
    String extra = queryPlanDto.getExtra();
    List<Plan> planList = planMapper.selectList(Wrappers.<Plan>lambdaQuery()
        .le(budget != 0, Plan::getMonthlyRent, budget)
        .ge(callMinutes != 0, Plan::getCallMinutes, callMinutes)
        .like(!data.isEmpty(), Plan::getData, data)
        .like(!extra.isEmpty(), Plan::getExtraServices, extra)
    );

    return planMapper.selectList(null);
    // return planList;
  }

  // 初始化示例数据（根据题目表格）
  @PostConstruct
  void initSampleData() {
    addPlan(new Plan(null, "经济卡", 39.0, "10GB", 100, "无", 0, "首月半价"));
    addPlan(new Plan(null, "畅享全家享", 99.0, "50GB", 500, "200M宽带+2张副卡", 12, "送视频会员月卡"));
    addPlan(new Plan(null, "全球通尊享", 199.0, "不限量", 2000, "国际漫游+5G优先", 24, "机场贵宾厅2次/年"));
    addPlan(new Plan(null, "学生青春卡", 59.0, "30GB", 200, "校园网加速", 6, "免流特定APP"));
  }
}