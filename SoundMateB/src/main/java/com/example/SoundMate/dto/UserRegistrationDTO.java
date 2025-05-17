package com.example.SoundMate.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDTO {
    private String email;
    private String password;
    private String displayName;
    private int age;
    private String gender;
}
