package com.example.SoundMate.repository;

import com.example.SoundMate.entity.RecommendedSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

@Repository
public interface RecommendedSongRepository extends JpaRepository<RecommendedSong, Long> {

    List<RecommendedSong> findByUserId(String userId);

    @Query("SELECT COUNT(r) > 0 FROM RecommendedSong r " +
       "WHERE LOWER(r.userId) = LOWER(:userId) " +
       "AND LOWER(r.trackName) = LOWER(:trackName) " +
       "AND LOWER(r.artistName) = LOWER(:artistName)")
boolean existsByUserIdAndTrackNameAndArtistName(
    @Param("userId") String userId,
    @Param("trackName") String trackName,
    @Param("artistName") String artistName
);

    void deleteByUserIdAndTrackNameAndArtistName(String userId, String trackName, String artistName);
} 
