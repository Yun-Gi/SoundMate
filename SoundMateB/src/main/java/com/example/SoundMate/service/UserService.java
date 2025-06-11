package com.example.SoundMate.service;

import com.example.SoundMate.entity.User;
import com.example.SoundMate.repository.ChatbotDataRepository;
import com.example.SoundMate.repository.UserPreferredGenresRepository;
import com.example.SoundMate.repository.UserRepository;
import com.example.SoundMate.repository.UserSavedMusicRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ChatbotDataRepository chatbotDataRepository;
    private final UserPreferredGenresRepository userPreferredGenresRepository;
    private final UserSavedMusicRepository userSavedMusicRepository;

    // 생성자 주입
    public UserService(
        UserRepository userRepository, 
        ChatbotDataRepository chatbotDataRepository,
        UserPreferredGenresRepository userPreferredGenresRepository, 
        UserSavedMusicRepository userSavedMusicRepository
        ) {
        this.userRepository = userRepository;
        this.chatbotDataRepository = chatbotDataRepository;
        this.userPreferredGenresRepository = userPreferredGenresRepository;
        this.userSavedMusicRepository = userSavedMusicRepository;
    }

    // 사용자 ID로 유저 찾기
    public Optional<User> findUserById(String uid) {
        return userRepository.findById(uid);
    }

    // 사용자 저장
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    // 사용자 데이터 삭제
    @Transactional
    public void deleteUserAndRelatedData(String userId) {
        chatbotDataRepository.deleteByUser_UserId(userId);
        userPreferredGenresRepository.deleteByUser_UserId(userId);
        userSavedMusicRepository.deleteById_UserId(userId);

        userRepository.deleteById(userId);
    }

    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }
}
