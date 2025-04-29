package com.example.SoundMate.service;

import com.example.SoundMate.dto.ChatRequestDTO;
import com.example.SoundMate.dto.ChatResponseDTO;
import com.example.SoundMate.dto.UserInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> analyzeText(String userMessage, UserInfoDTO userInfo) {
        String prompt =
            "사용자 정보: 나이 " + userInfo.getAge() +
            ", 성별 " + userInfo.getGender() +
            ", 선호 장르 " + userInfo.getGenre() + ". " +
            "다음 텍스트를 기반으로 음악의 특성을 예측해서 정확한 JSON만 반환해. " +
            "모든 값은 0.0 ~ 1.0 사이 소수 세번째까지 자세히, tempo는 30.0 ~ 250.0 사이의 BPM(비트) 값으로 응답해야 해. " +
            "다음 구조를 따라:\n\n" +
            "{\n" +
            "  \"acousticness\": 0.000,\n" +
            "  \"danceability\": 0.000,\n" +
            "  \"energy\": 0.000,\n" +
            "  \"valence\": 0.000,\n" +
            "  \"speechiness\": 0.000,\n" +
            "  \"instrumentalness\": 0.000,\n" +
            "  \"liveness\": 0.000,\n" +
            "  \"tempo\": 120.0\n" +
            "}\n\n" +
            "텍스트: \"" + userMessage + "\"";

        try {
            ChatRequestDTO.Message message = new ChatRequestDTO.Message("user", prompt);
            ChatRequestDTO request = new ChatRequestDTO("gpt-4o", Collections.singletonList(message));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<ChatRequestDTO> entity = new HttpEntity<>(request, headers);
            ResponseEntity<ChatResponseDTO> response = restTemplate.postForEntity(apiUrl, entity, ChatResponseDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String content = response.getBody().getChoices().get(0).getMessage().getContent();
                System.out.println("GPT 응답 (text):\n" + content);
                return parseMusicFeatures(content);
            } else {
                return Map.of("error", "GPT 응답 오류");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "텍스트 분석 중 오류 발생: " + e.getMessage());
        }
    }

    public Map<String, Object> analyzeImage(MultipartFile image) {
        try {
            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String imageDataUrl = "data:image/png;base64," + base64Image;

            String prompt =
                "이 이미지를 보고 음악 특성들을 분석해 JSON만 반환해. " +
                "모든 값은 0.0 ~ 1.0 사이 소수 세번째까지 자세히, tempo는 30.0 ~ 250.0 BPM 값으로 응답해야 해. " +
                "구조는 다음과 같아:\n" +
                "{\n" +
                "  \"acousticness\": 0.000,\n" +
                "  \"danceability\": 0.000,\n" +
                "  \"energy\": 0.000,\n" +
                "  \"valence\": 0.000,\n" +
                "  \"speechiness\": 0.000,\n" +
                "  \"instrumentalness\": 0.000,\n" +
                "  \"liveness\": 0.000,\n" +
                "  \"tempo\": 120.0\n" +
                "}";

            Map<String, Object> imagePart = Map.of(
                    "type", "image_url",
                    "image_url", Map.of("url", imageDataUrl)
            );
            Map<String, Object> textPart = Map.of(
                    "type", "text",
                    "text", prompt
            );

            Map<String, Object> message = Map.of(
                    "role", "user",
                    "content", List.of(textPart, imagePart)
            );

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o",
                    "messages", List.of(message)
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey.replace("Bearer ", ""));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                Map<String, Object> messageObj = (Map<String, Object>) choices.get(0).get("message");
                String content = (String) messageObj.get("content");

                System.out.println("GPT 응답 (image):\n" + content);
                return parseMusicFeatures(content);
            } else {
                return Map.of("error", "GPT 응답 오류");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "이미지 분석 중 오류 발생: " + e.getMessage());
        }
    }

    public Map<String, Object> analyzeTextAndImage(String message, MultipartFile image, UserInfoDTO userInfo) {
        try {
            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String imageDataUrl = "data:image/png;base64," + base64Image;

            String prompt =
                "사용자 정보: 나이 " + userInfo.getAge() +
                ", 성별 " + userInfo.getGender() +
                ", 선호 장르 " + userInfo.getGenre() + ". " +
                "텍스트와 이미지를 바탕으로 음악 특성을 분석해 JSON만 반환해. " +
                "모든 값은 0.0 ~ 1.0 사이 소수 세번째까지 자세히, tempo는 30.0 ~ 250.0 BPM 값으로 응답해야 해. " +
                "구조는 다음과 같아:\n" +
                "{\n" +
                "  \"acousticness\": 0.000,\n" +
                "  \"danceability\": 0.000,\n" +
                "  \"energy\": 0.000,\n" +
                "  \"valence\": 0.000,\n" +
                "  \"speechiness\": 0.000,\n" +
                "  \"instrumentalness\": 0.000,\n" +
                "  \"liveness\": 0.000,\n" +
                "  \"tempo\": 120.0\n" +
                "}\n\n" +
                "텍스트: \"" + message + "\"";

            Map<String, Object> imagePart = Map.of(
                    "type", "image_url",
                    "image_url", Map.of("url", imageDataUrl)
            );
            Map<String, Object> textPart = Map.of(
                    "type", "text",
                    "text", prompt
            );

            Map<String, Object> chatMessage = Map.of(
                    "role", "user",
                    "content", List.of(textPart, imagePart)
            );

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o",
                    "messages", List.of(chatMessage)
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey.replace("Bearer ", ""));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                Map<String, Object> messageObj = (Map<String, Object>) choices.get(0).get("message");
                String content = (String) messageObj.get("content");

                System.out.println("GPT 응답 (combined):\n" + content);
                return parseMusicFeatures(content);
            } else {
                return Map.of("error", "GPT 응답 오류");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "텍스트+이미지 분석 중 오류 발생: " + e.getMessage());
        }
    }

    private Map<String, Object> parseMusicFeatures(String content) {
        try {
            content = content.replaceAll("“|”", "\"").trim();

            if (content.startsWith("```json")) {
                content = content.replaceFirst("```json", "").trim();
            }
            if (content.endsWith("```")) {
                content = content.substring(0, content.length() - 3).trim();
            }

            System.out.println("💡 정제된 content:\n" + content);

            if (content.startsWith("{") && content.endsWith("}")) {
                Map<String, Object> json = objectMapper.readValue(content, Map.class);
                if (json.isEmpty()) {
                    return Map.of("error", "응답은 JSON이지만 필드가 비어있습니다.");
                }
                return json;
            }

            return Map.of("error", "JSON 형식이 아닙니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "파싱 중 오류 발생: " + e.getMessage());
        }
    }
}
