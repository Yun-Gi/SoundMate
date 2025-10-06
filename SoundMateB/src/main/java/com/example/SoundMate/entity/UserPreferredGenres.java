package com.example.SoundMate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_preferred_genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferredGenres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; 

    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    private Genres genre;
}