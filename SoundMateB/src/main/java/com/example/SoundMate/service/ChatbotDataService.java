package com.example.SoundMate.service;

import com.example.SoundMate.entity.ChatbotData;
import com.example.SoundMate.repository.ChatbotDataRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatbotDataService {

    private final ChatbotDataRepository chatbotDataRepository;

    // 생성자 주입
    public ChatbotDataService(ChatbotDataRepository chatbotDataRepository) {
        this.chatbotDataRepository = chatbotDataRepository;
    }

    // ID로 데이터 찾기
    public Optional<ChatbotData> findChatbotDataById(Long cid) {
        return chatbotDataRepository.findById(cid);
    }

    // 데이터 저장
    public ChatbotData saveChatbotData(ChatbotData chatbotData) {
        return chatbotDataRepository.save(chatbotData);
    }

}
