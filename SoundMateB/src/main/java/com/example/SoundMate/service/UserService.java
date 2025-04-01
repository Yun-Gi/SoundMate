package com.example.SoundMate.service;

import com.example.SoundMate.entity.User;
import com.example.SoundMate.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    // 생성자 주입
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 사용자 ID로 유저 찾기
    public Optional<User> findUserById(String uid) {
        return userRepository.findById(uid);
    }

    // 사용자 저장
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
}
