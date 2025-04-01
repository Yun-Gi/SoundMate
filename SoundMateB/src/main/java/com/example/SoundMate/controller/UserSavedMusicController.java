package com.example.SoundMate.controller;

import com.example.SoundMate.entity.UserSavedMusic;
import com.example.SoundMate.entity.UserSavedMusicId;
import com.example.SoundMate.service.UserSavedMusicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/saved-music")
public class UserSavedMusicController {

    private final UserSavedMusicService userSavedMusicService;

    public UserSavedMusicController(UserSavedMusicService userSavedMusicService) {
        this.userSavedMusicService = userSavedMusicService;
    }

    // 저장된 음악 단일 조회
    @GetMapping
    public ResponseEntity<UserSavedMusic> getSavedMusic(
            @RequestParam String userId,
            @RequestParam String youtubeLink
    ) {
        UserSavedMusicId id = new UserSavedMusicId();
        id.setUserId(userId);
        id.setYoutubeLink(youtubeLink);

        Optional<UserSavedMusic> result = userSavedMusicService.findUserSavedMusicById(id);
        return result.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 저장할 음악 저장
    @PostMapping
    public ResponseEntity<UserSavedMusic> saveMusic(@RequestBody UserSavedMusic music) {
        return ResponseEntity.ok(userSavedMusicService.saveUserSavedMusic(music));
    }

    // 저장된 음악 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteSavedMusic(
            @RequestParam String userId,
            @RequestParam String youtubeLink) {
        
        UserSavedMusicId id = new UserSavedMusicId(userId, youtubeLink);
        boolean deleted = userSavedMusicService.deleteByIdIfExists(id);

        return deleted ? ResponseEntity.noContent().build()
                    : ResponseEntity.notFound().build();
    }
}

