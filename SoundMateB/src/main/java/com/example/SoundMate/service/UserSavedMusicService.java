package com.example.SoundMate.service;

import com.example.SoundMate.entity.UserSavedMusic;
import com.example.SoundMate.entity.UserSavedMusicId;
import com.example.SoundMate.repository.UserSavedMusicRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSavedMusicService {
    
    private final UserSavedMusicRepository userSavedMusicRepository;

    // 생성자 주입
    public UserSavedMusicService(UserSavedMusicRepository userSavedMusicRepository) {
        this.userSavedMusicRepository = userSavedMusicRepository;
    }

    // ID로 사용자가 저장한 음악 찾기
    public Optional<UserSavedMusic> findUserSavedMusicById(UserSavedMusicId sid) {
        return userSavedMusicRepository.findById(sid);
    }

    // 저장할 음악 저장
    public UserSavedMusic saveUserSavedMusic(UserSavedMusic userSavedMusic) {
        return userSavedMusicRepository.save(userSavedMusic);
    }

    // 저장한 음악 삭제
    public boolean deleteByIdIfExists(UserSavedMusicId id) {
        if (userSavedMusicRepository.existsById(id)) {
            userSavedMusicRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
