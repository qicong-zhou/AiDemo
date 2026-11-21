package org.example.aidemo.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Component
public class AiUtils {

    @Value("${deepseek.api.key}")
    private String key;


    public String aiGetTag(String title,String content) throws Exception{

            HttpClient client = HttpClient.newHttpClient();
            String prompt = "请阅读备忘录，提取不超过3个标签，用英文逗号隔开，禁止输出废话。示例：工作,生活,天气。内容：" + title + "，" + content;
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> message = Map.of(
                "role", "user",
                "content", prompt);
            Map<String, Object> body = Map.of(
                "model", "deepseek-chat",
                "messages", List.of(message));

            String jsonBody = mapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.deepseek.com/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + key)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            String aiReply = root.get("choices").get(0).get("message").get("content").asString();
            return aiReply.replaceAll("[^\\u4e00-\\u9fa5,]", "");
    }
}
