package com.luruoyang;

import com.fasterxml.jackson.core.util.VersionUtil;
import com.luruoyang.util.VectorDistanceUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author luruoyang
 */
@SpringBootTest
public class EmbeddingTest {
  @Autowired
  private OpenAiEmbeddingModel embeddingModel;

  private final String query = "global conflicts";
  String[] texts = new String[]{
      "俄罗斯和乌克兰发生了武装冲突",
      "哈马斯称加沙下阶段停火谈判仍在进行 以方尚未做出承诺",
      "土耳其、芬兰、瑞典与北约代表将继续就瑞典“入约”问题进行谈判",
      "日本航空基地水井中检测出有机氟化物超标",
      "国家游泳中心（水立方）：恢复游泳、嬉水乐园等水上项目运营",
      "我国首次在空间站开展舱外辐射生物学暴露实验",
  };

  @Test
  @DisplayName("向量距离测试")
  public void testDistance() {
    String input = "俄罗斯和乌克兰发生了武装冲突";
    float[] embed = embeddingModel.embed(input);
    System.out.println(Arrays.toString(embed));
  }

  @Test
  @DisplayName("余弦距离")
  public void testCosDistance() {
    float[] embed = embeddingModel.embed(query);
    List<float[]> textVector = embeddingModel.embed(Arrays.asList(texts));

    // 先比较自己
    System.out.println(VectorDistanceUtils.cosineDistance(embed, embed));

    // 再比较texts
    textVector.forEach(vector -> System.out.println(VectorDistanceUtils.cosineDistance(embed, vector)));

  }

  @Test
  @DisplayName("欧几里得距离")
  public void testEuclideanDistance() {
    float[] embed = embeddingModel.embed(query);
    List<float[]> textVector = embeddingModel.embed(Arrays.asList(texts));

    // 先比较自己
    System.out.println(VectorDistanceUtils.euclideanDistance(embed, embed));

    // 再比较texts
    textVector.forEach(vector -> System.out.println(VectorDistanceUtils.euclideanDistance(embed, vector)));

  }

}
