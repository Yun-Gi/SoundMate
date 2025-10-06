package com.example.SoundMate.dto;

public class UserInfoDTO {
    private String userId;
    private String gender;
    private int age;
    private String genre;

    public UserInfoDTO() {}

    public UserInfoDTO(String userId, String gender, int age, String genre) {
        this.userId = userId;
        this.gender = gender;
        this.age = age;
        this.genre = genre;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
}
