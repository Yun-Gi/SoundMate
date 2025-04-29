package com.example.SoundMate.controller;

import com.example.SoundMate.dto.UserInfoDTO;
import com.example.SoundMate.service.OpenAIService;
import com.example.SoundMate.service.DatasetRecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OpenAIService openAIService;
    private final DatasetRecommendationService datasetRecommendationService;

    public ChatController(OpenAIService openAIService, DatasetRecommendationService datasetRecommendationService) {
        this.openAIService = openAIService;
        this.datasetRecommendationService = datasetRecommendationService;
    }

    @GetMapping
    public String hello() {
        return "SoundMate입니다! 노래추천 할 수 있어요.";
    }

    @PostMapping("/text")
    public ResponseEntity<Map<String, Object>> analyzeMusicFromText(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        UserInfoDTO userInfo = new UserInfoDTO("여성", 23, "발라드");

        Map<String, Object> result = openAIService.analyzeText(userMessage, userInfo);
        if (result.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> analyzeMusicFromImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = openAIService.analyzeImage(file);
        if (result.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/combined")
    public ResponseEntity<Map<String, Object>> analyzeMusicFromTextAndImage(
            @RequestParam("message") String message,
            @RequestParam("file") MultipartFile file) {

        UserInfoDTO userInfo = new UserInfoDTO("여성", 23, "발라드");
        Map<String, Object> result = openAIService.analyzeTextAndImage(message, file, userInfo);
        if (result.containsKey("error")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    // 통합 추천 API (텍스트만 / 이미지만 / 둘 다 입력 가능)
    @PostMapping(value = "/recommend", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Map<String, Object>>> recommendFromTextAndOrImage(
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        UserInfoDTO userInfo = new UserInfoDTO("여성", 23, "발라드");
        Map<String, Object> features;

        try {
            if (message != null && file != null && !file.isEmpty()) {
                // 텍스트 + 이미지 분석
                features = openAIService.analyzeTextAndImage(message, file, userInfo);
            } else if (message != null) {
                // 텍스트만 분석
                features = openAIService.analyzeText(message, userInfo);
            } else if (file != null && !file.isEmpty()) {
                // 이미지만 분석
                features = openAIService.analyzeImage(file);
            } else {
                return ResponseEntity.badRequest().body(List.of(Map.of("error", "텍스트나 이미지를 최소 하나 입력해야 합니다.")));
            }

            System.out.println("GPT 분석 결과 features:\n" + features);

            if (features.containsKey("error")) {
                return ResponseEntity.badRequest().body(List.of(Map.of("error", features.get("error"))));
            }

            List<Map<String, Object>> recommendedSongs = datasetRecommendationService.recommendSongs(features);
            System.out.println("추천된 곡 개수: " + recommendedSongs.size());
            return ResponseEntity.ok(recommendedSongs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of(Map.of("error", "서버 처리 중 오류 발생: " + e.getMessage())));
        }
    }
}
