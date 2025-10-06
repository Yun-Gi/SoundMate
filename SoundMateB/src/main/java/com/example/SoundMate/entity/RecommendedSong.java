package com.example.SoundMate.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommended_songs")
public class RecommendedSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // 사용자 식별용 (로그인 또는 UUID)

    private String trackName;

    private String artistName;

    private String youtubeUrl;

    private LocalDateTime recommendedAt = LocalDateTime.now();

    public RecommendedSong() {
    }

    public RecommendedSong(String userId, String trackName, String artistName, String youtubeUrl) {
        this.userId = userId;
        this.trackName = trackName;
        this.artistName = artistName;
        this.youtubeUrl = youtubeUrl;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public LocalDateTime getRecommendedAt() {
        return recommendedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public void setRecommendedAt(LocalDateTime recommendedAt) {
        this.recommendedAt = recommendedAt;
    }
} 