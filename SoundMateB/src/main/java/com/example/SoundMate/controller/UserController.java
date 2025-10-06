package com.example.SoundMate.controller;

import com.example.SoundMate.dto.UserInfoDTO;
import com.example.SoundMate.dto.UserRegistrationDTO;
import com.example.SoundMate.dto.GoogleUserRegistrationDTO;
import com.example.SoundMate.entity.User;
import com.example.SoundMate.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserInfo;
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

    // 유저 정보 조회
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestParam("uid") String uid) {
        Optional<User> user = userService.findUserById(uid);
        if (user.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("gender", user.get().getGender());
            response.put("age", user.get().getAge());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    // 유저 정보 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") String id, @RequestBody UserInfoDTO userInfo) {
        Optional<User> userOptional = userService.findUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setGender(userInfo.getGender());
            user.setAge(userInfo.getAge());
            userService.saveUser(user);
            return ResponseEntity.ok("User updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    
    }

    @PostMapping("/register/google")
    public ResponseEntity<?> registerGoogleUser(@RequestBody GoogleUserRegistrationDTO userDto) {
        // 이미 구글에서 인증 받은 정보임. 여기서 Firebase에 또 유저 만들 필요 X
        // 그냥 userDto에서 uid/email/displayName 등 DB에만 저장
        // 이미 있으면 중복 저장 안하고, 없으면 신규 등록
        if (userService.existsByUserId(userDto.getUid())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 등록된 사용자");
        }

        User user = new User();
        user.setUserId(userDto.getUid());
        user.setEmail(userDto.getEmail());
        user.setDisplayName(userDto.getDisplayName());
        user.setAge(userDto.getAge());
        user.setGender(userDto.getGender());
        userService.saveUser(user);

        return ResponseEntity.ok("회원가입 성공");
    }
}
