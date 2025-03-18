package com.example.SoundMate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chatbot_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class ChatbotData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long chatId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Lob
    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "sender", nullable = false)
    private String sender;
}
