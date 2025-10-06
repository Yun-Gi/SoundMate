package com.example.SoundMate.repository;

import com.example.SoundMate.entity.ChatbotData;
import com.example.SoundMate.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatbotDataRepository extends JpaRepository<ChatbotData, Long>{
    void deleteByUser_UserId(String userId);
    List<ChatbotData> findByUser(User user); 
}
