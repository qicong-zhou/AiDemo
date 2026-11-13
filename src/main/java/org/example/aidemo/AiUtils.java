package org.example.aidemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@Component
public class AiUtils {

    @Value("${deepseek.api.key}")
    private String key;


    public String aiGetTag(String title,String content) throws Exception{

        try(HttpClient client = HttpClient.newHttpClient()) {
            String prompt = "请阅读以下备忘录，提取不超过3个标签（用逗号隔开，不要其他任何废话）。标题：" + title + "，正文：" + content;

            String jsonBody = """
                    {"model":"deepseek-chat",
                    "messages":[
                    {"role":"user","content":"%s"}
                    ]}
                    """.formatted(prompt);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.deepseek.com/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + key)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            return root.get("choices").get(0).get("message").get("content").asString();
        }
    }
}
