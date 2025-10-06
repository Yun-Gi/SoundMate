package com.example.SoundMate.dto;

public class ImageUploadResponseDTO {
    private String emotion;
    private String mood;
    private String weather;

    public ImageUploadResponseDTO() {}

    public ImageUploadResponseDTO(String emotion, String mood, String weather) {
        this.emotion = emotion;
        this.mood = mood;
        this.weather = weather;
    }

    public String getEmotion() { return emotion; }
    public void setEmotion(String emotion) { this.emotion = emotion; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }
}
