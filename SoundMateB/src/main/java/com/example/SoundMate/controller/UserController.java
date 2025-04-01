package com.example.SoundMate.controller;

import com.example.SoundMate.entity.User;
import com.example.SoundMate.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CURD 생성
    //
}
