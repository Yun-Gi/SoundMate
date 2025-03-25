package com.example.SoundMate.repository;

import com.example.SoundMate.entity.ChatbotData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatbotDataRepository extends JpaRepository<ChatbotData, Long>{
    
}
