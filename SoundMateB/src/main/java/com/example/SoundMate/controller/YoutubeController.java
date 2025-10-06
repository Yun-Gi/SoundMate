package com.example.SoundMate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.SoundMate.service.YoutubeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class YoutubeController {

    private final YoutubeService youtubeService;

    @GetMapping("/search")
    public ResponseEntity<String> getTopVideo(@RequestParam("keyword") String keyword) {
        String url = youtubeService.searchTopVideoUrl(keyword);
        return url != null ? ResponseEntity.ok(url)
                           : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
