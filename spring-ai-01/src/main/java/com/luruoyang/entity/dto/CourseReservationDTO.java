package com.luruoyang.entity.dto;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author luruoyang
 */
@Data
public class CourseReservationDTO {
  @ToolParam(required = true, description = "课程名称")
  private String courseName;
  @ToolParam(required = true, description = "学生姓名")
  private String studentName;
  @ToolParam(required = true, description = "联系方式")
  private String contactInfo;
  @ToolParam(required = true, description = "校区名称")
  private String schoolName;
  @ToolParam(required = true, description = "备注")
  private String remark;
}
