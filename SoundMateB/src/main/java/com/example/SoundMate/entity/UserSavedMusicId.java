package com.example.SoundMate.entity;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class UserSavedMusicId implements Serializable {
    
    @Column(name = "user_id")
    private String userId;

    @Column(name = "youtube_link")
    private String youtubeLink;
}