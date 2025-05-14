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
    // 유저 조회
    @GetMapping
    public ResponseEntity<User> getUser(@RequestParam String id) {
        Optional<User> result = userService.findUserById(id);
        return result.map(ResponseEntity::ok)
                 .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 유저 삭제(파이어 베이스에서 삭제하고 남은 것들)
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestParam String id) {
        Optional<User> user = userService.findUserById(id);
        if (user.isPresent()) {
            userService.deleteUserAndRelatedData(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 유저 생성
    // @PostMapping
    // public ResponseEntity<User> createUser(@RequestBody User user) {
        
    // }
}
