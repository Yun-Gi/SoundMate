package com.example.SoundMate.service;

import com.example.SoundMate.dto.ChatRequestDTO;
import com.example.SoundMate.dto.ChatResponseDTO;
import com.example.SoundMate.dto.UserInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String recommendSong(String userMessage, UserInfoDTO userInfo) {
        String prompt = "사용자 정보: 나이 " + userInfo.getAge() +
                        ", 성별 " + userInfo.getGender() +
                        ", 선호 장르 " + userInfo.getGenre() + ". " +
                        "다음 텍스트의 감정을 분석하고, 그에 맞는 한국 노래 한 곡을 추천해줘. 노래 제목과 가수를 알려줘. 텍스트: \"" + userMessage + "\"";

        ChatRequestDTO.Message message = new ChatRequestDTO.Message("user", prompt);
        ChatRequestDTO request = new ChatRequestDTO("gpt-4o", Collections.singletonList(message));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey.replace("Bearer ", ""));

        HttpEntity<ChatRequestDTO> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ChatResponseDTO> response = restTemplate.postForEntity(apiUrl, entity, ChatResponseDTO.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().getChoices().get(0).getMessage().getContent();
        } else {
            return "Error: Unable to get recommendation.";
        }
    }

    public String analyzeImage(MultipartFile image) {
        return "이미지 분석 기능은 추후 GPT-4o Vision API 연동 필요";
    }
}
