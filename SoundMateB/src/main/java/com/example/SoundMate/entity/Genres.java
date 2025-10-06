package com.example.SoundMate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class Genres {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "genre_id")
    private Integer genreId;

    @Column(name = "genre_name", nullable = false, unique = true)
    private String genreName;
}
