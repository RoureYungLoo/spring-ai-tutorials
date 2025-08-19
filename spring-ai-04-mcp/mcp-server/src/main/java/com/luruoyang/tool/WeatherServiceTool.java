package com.luruoyang.tool;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.luruoyang.domain.dto.WeatherDTO;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * @author luruoyang
 */
@Component
public class WeatherServiceTool {
  @Tool(description = "根据城市id查询天气信息")
  public WeatherDTO getWeather(@ToolParam(description = "城市id") String cityId) {
    //1.声明天气接口地址
    String url = "http://t.weather.itboy.net/api/weather/city/" + cityId;
    //2.利用hutool工具远程调用天气
    String data = HttpUtil.get(url);
    //3.解析json字符串数据为JSON对象
    JSONObject jsonObject = JSONUtil.parseObj(data);

    // 4.封装数据到返回值对象中
    return WeatherDTO.builder()
        .cityId(cityId)//城市id
        .city(jsonObject.getByPath("cityInfo.city", String.class))//城市名称
        .temperature(jsonObject.getByPath("data.wendu", String.class))//当前温度
        .lowTemperature(jsonObject.getByPath("data.forecast[0].low", String.class))//低温
        .highTemperature(jsonObject.getByPath("data.forecast[0].high", String.class))//高温
        .date(jsonObject.getByPath("date", String.class))//数据日期
        .quality(jsonObject.getByPath("data.quality", String.class))//空气质量
        .pm25(jsonObject.getByPath("data.pm25", Double.class))//PM2.5 浓度
        .build();
  }
}