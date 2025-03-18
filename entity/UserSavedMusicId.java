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
    
    private String userId;
    private String youtubeLink;
}