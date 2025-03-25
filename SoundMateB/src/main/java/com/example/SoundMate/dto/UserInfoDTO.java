package com.example.SoundMate.dto;

public class UserInfoDTO {
    private String gender;
    private int age;
    private String genre;

    public UserInfoDTO() {}

    public UserInfoDTO(String gender, int age, String genre) {
        this.gender = gender;
        this.age = age;
        this.genre = genre;
    }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
}
