package com.example.SoundMate.controller;

import com.example.SoundMate.dto.UserRegistrationDTO;
import com.example.SoundMate.entity.User;
import com.example.SoundMate.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
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

    //유저 생성
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO userDto) {
        try {
            // Firebase에서 사용자 생성
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(userDto.getEmail())
                    .setPassword(userDto.getPassword()) // 비밀번호는 Firebase에서 관리
                    .setDisplayName(userDto.getDisplayName())
                    .setEmailVerified(false);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

            // Firebase에서 생성된 사용자 ID (UID)
            String uid = userRecord.getUid();

            // User 엔티티 생성
            User user = new User();
            user.setUserId(uid);
            user.setEmail(userDto.getEmail());
            user.setDisplayName(userDto.getDisplayName());
            user.setAge(userDto.getAge());
            user.setGender(userDto.getGender());
            user.setGenre(userDto.getGenre());

            // MySQL에 사용자 정보 저장
            userService.saveUser(user);

            // 사용자 ID로 Firebase에서 ID Token 생성
            String idToken = FirebaseAuth.getInstance().createCustomToken(uid);

            // 클라이언트로 ID Token 반환 (자동 로그인)
            Map<String, Object> response = new HashMap<>();
            response.put("message", "회원가입 성공");
            response.put("idToken", idToken);

            return ResponseEntity.ok("회원가입 성공");
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패: " + e.getMessage());
        }
    }
}
