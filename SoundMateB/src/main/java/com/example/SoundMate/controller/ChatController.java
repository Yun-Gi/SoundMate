package com.example.SoundMate.controller;

import com.example.SoundMate.dto.UserInfoDTO;
import com.example.SoundMate.service.OpenAIService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OpenAIService openAIService;

    public ChatController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @GetMapping
    public String hello() {
        return "SoundMate입니다! 노래추천 할 수 있어요.";
    }

    @PostMapping("/text")
    public Map<String, String> recommendSong(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        UserInfoDTO userInfo = new UserInfoDTO("여성", 23, "발라드");
        String recommendation = openAIService.recommendSong(userMessage, userInfo);
        return Map.of("response", recommendation);
    }

    @PostMapping("/image")
    public Map<String, String> analyzeImage(@RequestParam("file") MultipartFile file) {
        String result = openAIService.analyzeImage(file);
        return Map.of("response", result);
    }
}
