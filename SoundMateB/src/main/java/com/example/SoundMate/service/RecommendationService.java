package com.example.SoundMate.service;

import com.example.SoundMate.entity.RecommendedSong;
import com.example.SoundMate.repository.RecommendedSongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 import

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RecommendedSongRepository recommendedSongRepository;

    public RecommendationService(RecommendedSongRepository recommendedSongRepository) {
        this.recommendedSongRepository = recommendedSongRepository;
    }

    public void saveRecommendation(String userId, String trackName, String artistName, String youtubeUrl) {
        if (!recommendedSongRepository.existsByUserIdAndTrackNameAndArtistName(userId, trackName, artistName)) {
            RecommendedSong song = new RecommendedSong(userId, trackName, artistName, youtubeUrl);
            recommendedSongRepository.save(song);
        }
    }

    public List<String> getRecommendedTrackIds(String userId) {
        return recommendedSongRepository.findByUserId(userId).stream()
                .map(song -> song.getTrackName() + "|" + song.getArtistName())
                .collect(Collectors.toList());
    }

    public List<RecommendedSong> getRecommendations(String userId) {
        return recommendedSongRepository.findByUserId(userId);
    }

    @Transactional // 삭제 로직에 트랜잭션 적용
    public void deleteRecommendation(String userId, String trackName, String artistName) {
        recommendedSongRepository.deleteByUserIdAndTrackNameAndArtistName(userId, trackName, artistName);
    }
}
