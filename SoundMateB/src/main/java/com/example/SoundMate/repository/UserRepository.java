package com.example.SoundMate.repository;

import com.example.SoundMate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserId(String userId);
    
}

