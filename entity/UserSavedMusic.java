package com.example.SoundMate.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_saved_music")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class UserSavedMusic {

    @EmbeddedId
    private UserSavedMusicId id;

    @Column(name = "music_title", nullable = false)
    private String musicTitle;

    @Column(name = "artist", nullable = false)
    private String artist;
}
