package com.example.SoundMate.repository;

import com.example.SoundMate.entity.UserPreferredGenres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferredGenresRepository extends JpaRepository<UserPreferredGenres, Long> {
    boolean existsByUser_UserIdAndGenre_GenreId(String userId, Integer genreId);
    void deleteByUser_UserId(String userId);
}
