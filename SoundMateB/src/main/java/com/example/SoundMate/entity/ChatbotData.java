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
    @Column(name = "chat_id")
    private Long chatId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Lob
    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "sender", nullable = false)
    private String sender;
}
