package com.example.SoundMate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User { // 일단 이 쪽은 파이어베이스 객체가 어떻게 올 지 몰라서

    @Id
    @Column(name = "user_id")
    private String userId; 

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "age")
    private int age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "genre")
    private String genre;
}

