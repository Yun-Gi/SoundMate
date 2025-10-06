package com.example.SoundMate.controller;

import com.example.SoundMate.entity.UserPreferredGenres;
import com.example.SoundMate.service.UserPreferredGenresService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/preferred")
public class UserPreferredGenresController {
    
    private final UserPreferredGenresService userPreferredGenresService;

    public UserPreferredGenresController(UserPreferredGenresService userPreferredGenresService) {
        this.userPreferredGenresService = userPreferredGenresService;
    }

    // CRUD 생성
    // 선호 장르 조회(프로그램이 선호 음악 추천 시에 읽어서 반영해야 함)
    @GetMapping
    public ResponseEntity<UserPreferredGenres> getPreferredGenres(@RequestParam Long id) {
        Optional<UserPreferredGenres> result = userPreferredGenresService.findUserPreferredGenresById(id);
        return result.map(ResponseEntity::ok)
                 .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // 선호 장르 등록(유저가 선호장르 등록시 넣어야 함) 나중에 함수명 필요 시 수정
    @PostMapping
    public ResponseEntity<UserPreferredGenres> saveGenres(@RequestBody UserPreferredGenres request) {
        UserPreferredGenres saved = userPreferredGenresService.savePreferredGenres(request);
        return ResponseEntity.ok(saved);
    }
    
    // 선호 장르 삭제(유저가 질린 장르 삭제)
    @DeleteMapping
    public ResponseEntity<Void> deleteGenres(@RequestParam Long id) {
        boolean deleted = userPreferredGenresService.deleteByIdIfExists(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); 
    }
}
