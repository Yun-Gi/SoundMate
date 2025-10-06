package com.example.SoundMate.controller;

import com.example.SoundMate.entity.RecommendedSong;
import com.example.SoundMate.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    // 추천 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<RecommendedSong>> getRecommendations(@PathVariable("userId") String userId) {
        List<RecommendedSong> recommendations = recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }

    // 추천 저장
    @PostMapping("/{userId}")
    public ResponseEntity<Void> saveRecommendation(
            @PathVariable("userId") String userId,
            @RequestParam("trackName") String trackName,
            @RequestParam("artistName") String artistName,
            @RequestParam(value = "youtubeUrl", required = false) String youtubeUrl) {
        try {
            recommendationService.saveRecommendation(userId, trackName, artistName, youtubeUrl);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();  // 서버 오류 발생 시 명시적 반환
        }
    }

    // 추천 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteRecommendation(
            @PathVariable("userId") String userId,
            @RequestParam("trackName") String trackName,
            @RequestParam("artistName") String artistName) {
        try {
            recommendationService.deleteRecommendation(userId, trackName, artistName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();  // 오류 메시지 출력 및 응답
        }
    }
}
