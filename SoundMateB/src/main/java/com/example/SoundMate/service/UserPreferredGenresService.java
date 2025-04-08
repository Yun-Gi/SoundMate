package com.example.SoundMate.service;

import com.example.SoundMate.entity.UserPreferredGenres;
import com.example.SoundMate.repository.UserPreferredGenresRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPreferredGenresService {
    
    private final UserPreferredGenresRepository userPreferredGenresRepository;

    // 생성자 주입
    public UserPreferredGenresService(UserPreferredGenresRepository userPreferredGenresRepository) {
        this.userPreferredGenresRepository = userPreferredGenresRepository;
    }

    // ID로 유저 선호 장르 찾기
    public Optional<UserPreferredGenres> findUserPreferredGenresById(Long pid) {
        return userPreferredGenresRepository.findById(pid);
    }

    // 유저 선호 장르 저장
    public UserPreferredGenres savePreferredGenres(UserPreferredGenres request) {
        if (userPreferredGenresRepository.existsByUser_UserIdAndGenre_GenreId(
            request.getUser().getUserId(), request.getGenre().getGenreId())) {
        throw new IllegalArgumentException("이미 선호 장르에 등록됨");
    }

        return userPreferredGenresRepository.save(request);
    }

    //유저 선호 장르 삭제
    public boolean deleteByIdIfExists(Long pid) {
        if (userPreferredGenresRepository.existsById(pid)) {
            userPreferredGenresRepository.deleteById(pid);
            return true;
        }
        return false;
    }
}
