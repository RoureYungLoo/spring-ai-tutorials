package com.luruoyang.tools;

import com.luruoyang.domain.dto.QueryPlanDto;
import com.luruoyang.domain.entity.Plan;
import com.luruoyang.repository.PlanRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author luruoyang
 */
@Component
public class ChinaMobileTool {

  @Autowired
  private PlanRepository planRepository;

  @Tool(description = "根据用户输入的条件查询套餐")
  public List<Plan> query(QueryPlanDto queryPlanDto) {
    return planRepository.queryPlans(queryPlanDto);
  }
}
