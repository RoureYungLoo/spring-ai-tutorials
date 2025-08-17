package com.luruoyang.tools;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.luruoyang.entity.dto.QueryCourseDTO;
import com.luruoyang.entity.pojo.Course;
import com.luruoyang.entity.pojo.CourseReservation;
import com.luruoyang.entity.pojo.School;
import com.luruoyang.service.ICourseReservationService;
import com.luruoyang.service.ICourseService;
import com.luruoyang.service.ISchoolService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author luruoyang
 */
@Component
public class CourseTools {

  @Autowired
  private ICourseService courseService;

  @Autowired
  private ISchoolService schoolService;

  @Autowired
  private ICourseReservationService courseReservationService;

  @Tool(description = "根据条件查询课程")
  public List<Course> queryCourse(QueryCourseDTO dto) {
    Integer edu = dto.getEdu();
    String type = dto.getType();
    List<QueryCourseDTO.Sort> sorts = dto.getSorts();

    LambdaQueryWrapper<Course> wrapper = Wrappers.lambdaQuery();
    wrapper.eq(StringUtils.hasText(type), Course::getType, type)
        .le(edu != null, Course::getEdu, edu);

    if (CollectionUtils.isNotEmpty(sorts)) {
      sorts.forEach(sort -> {
        wrapper.orderBy(true, sort.getAsc(), Course::getPrice);
      });
    }

    return courseService.list(wrapper);
  }

  @Tool(description = "查询校区列表")
  public List<School> querySchoolList() {
    return schoolService.list();
  }

  @Tool(description = "新增试听预约单,并返回预约单号")
  public String addCourseReservation(
      @ToolParam(required = true, description = "课程名称")
      String courseName,
      @ToolParam(required = true, description = "学生姓名")
      String studentName,
      @ToolParam(required = true, description = "联系方式")
      String contactInfo,
      @ToolParam(required = true, description = "校区名称")
      String schoolName,
      @ToolParam(required = true, description = "备注")
      String remark
  ) {
    CourseReservation entity = CourseReservation.builder()
        .course(courseName)
        .studentName(studentName)
        .contactInfo(contactInfo)
        .school(schoolName)
        .remark(remark)
        .build();
    courseReservationService.save(entity);

    return entity.getId().toString();

  }
}
