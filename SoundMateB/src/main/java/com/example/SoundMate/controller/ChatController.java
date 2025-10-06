package com.example.SoundMate.controller;

import com.example.SoundMate.dto.UserInfoDTO;
import com.example.SoundMate.service.OpenAIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.SoundMate.service.DatasetRecommendationService;
import com.example.SoundMate.dto.ConversationResponseDTO;
import com.example.SoundMate.dto.ConversationRequestDTO;
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
    public ResponseEntity<Map<String, Object>> analyzeMusicFromText(@RequestBody Map<String, Object> request) {
        String userMessage = (String) request.get("message");

        Map<String, Object> userInfoMap = (Map<String, Object>) request.get("userInfo");

        UserInfoDTO userInfo = new UserInfoDTO(
            (String) userInfoMap.get("userId"),
            (String) userInfoMap.get("gender"),
            ((Number) userInfoMap.get("age")).intValue(),
            (String) userInfoMap.get("genre")
        );

        Map<String, Object> result = openAIService.analyzeText(userMessage, userInfo);
        if (result.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ConversationResponseDTO> analyzeMusicFromImage(
            @RequestPart("file") MultipartFile file,
            @RequestPart("userInfo") String userInfoJson
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            UserInfoDTO userInfo = mapper.readValue(userInfoJson, UserInfoDTO.class);

            ConversationResponseDTO result = openAIService.analyzeImage(file, userInfo);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/combined")
    public ResponseEntity<ConversationResponseDTO> analyzeMusicFromTextAndImage(
            @RequestParam("message") String message,
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam("gender") String gender,
            @RequestParam("age") int age,
            @RequestParam("genre") String genre
    ) {
        UserInfoDTO userInfo = new UserInfoDTO(userId, gender, age, genre);
        ConversationResponseDTO result = openAIService.analyzeTextAndImage(message, file, userInfo);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/recommend", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> recommendFromTextAndOrImage(
            @RequestParam(value = "message", required = false) String message,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("userInfo") UserInfoDTO userInfo) {

        try {
            ConversationResponseDTO features;

            if (message != null && file != null && !file.isEmpty()) {
                features = openAIService.analyzeTextAndImage(message, file, userInfo);
            } else if (message != null) {
                features = openAIService.analyzeConversation(List.of(message), userInfo);
            } else if (file != null && !file.isEmpty()) {
                features = openAIService.analyzeImage(file, userInfo);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "텍스트나 이미지를 최소 하나 입력해야 합니다."));
            }

            if (!features.isRecommend()) {
                return ResponseEntity.ok(Map.of(
                    "reply", features.getReply(),
                    "recommend", false,
                    "emotion", features.getEmotion(),
                    "mood", features.getMood()
                ));
            }

            Map<String, Object> featureMap = Map.of(
                "acousticness", features.getAcousticness(),
                "danceability", features.getDanceability(),
                "energy", features.getEnergy(),
                "valence", features.getValence(),
                "speechiness", features.getSpeechiness(),
                "instrumentalness", features.getInstrumentalness(),
                "liveness", features.getLiveness(),
                "tempo", features.getTempo()
            );

            List<Map<String, Object>> recommendedSongs = datasetRecommendationService.recommendSongs(featureMap);

            return ResponseEntity.ok(Map.of(
                "reply", features.getReply(),
                "recommend", true,
                "emotion", features.getEmotion(),
                "mood", features.getMood(),
                "songs", recommendedSongs
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "서버 처리 중 오류 발생: " + e.getMessage()));
        }
    }

    @PostMapping("/conversation")
    public ResponseEntity<ConversationResponseDTO> analyzeConversation(@RequestBody ConversationRequestDTO request) {
        ConversationResponseDTO response = openAIService.analyzeConversation(request.getMessages(), request.getUserInfo());
        return ResponseEntity.ok(response);
    }
}
