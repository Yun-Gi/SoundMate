package com.example.SoundMate.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleUserRegistrationDTO {
    private String uid;           // 구글/Firebase UID (반드시 포함!)
    private String email;         // 이메일
    private String displayName;   // 이름 또는 닉네임
    private int age;              // 나이 (기본값 -1)
    private String gender;        // 성별 (기본값 "미설정")
}