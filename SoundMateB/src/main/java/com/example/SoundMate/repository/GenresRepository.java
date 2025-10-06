package com.example.SoundMate.repository;

import com.example.SoundMate.entity.Genres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenresRepository extends JpaRepository<Genres, Integer> {
}
