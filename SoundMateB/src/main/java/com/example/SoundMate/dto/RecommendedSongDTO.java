package com.example.SoundMate.dto;

public class RecommendedSongDTO {
    private String trackName;
    private String artistName;
    private String youtubeUrl;

    public RecommendedSongDTO() {
        // 기본 생성자
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }
}
