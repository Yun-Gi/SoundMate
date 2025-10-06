package com.example.SoundMate.repository;

import com.example.SoundMate.entity.UserSavedMusic;
import com.example.SoundMate.entity.UserSavedMusicId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSavedMusicRepository extends JpaRepository<UserSavedMusic, UserSavedMusicId> {
    void deleteById_UserId(String userId);
}
