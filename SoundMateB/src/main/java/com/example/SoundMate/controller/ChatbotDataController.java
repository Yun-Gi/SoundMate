package com.example.SoundMate.controller;

import com.example.SoundMate.entity.ChatbotData;
import com.example.SoundMate.service.ChatbotDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/chat-data")
public class ChatbotDataController {
    
    private final ChatbotDataService chatbotDataService;

    public ChatbotDataController(ChatbotDataService chatbotDataService) {
        this.chatbotDataService = chatbotDataService;
    }

    // 대화 저장
    @GetMapping
    public ResponseEntity<ChatbotData> getChatData(@RequestParam Long id) {
        Optional<ChatbotData> result = chatbotDataService.findChatbotDataById(id);
        return result.map(ResponseEntity::ok)
                 .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 대화 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteChatData(@RequestParam Long id) {
        boolean deleted = chatbotDataService.deleteByIdIfExists(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); 
    }

    // 모든 대화 삭제
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllChatData(@RequestParam String id) {
        chatbotDataService.deleteAllChatData(id);
        return ResponseEntity.noContent().build();
    }
}
