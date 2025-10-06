package com.example.soundmate

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

class AutoLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            // 자동 로그인 성공 → 메인 화면으로 이동
            val intent = Intent(this, ChatScreen::class.java)
            startActivity(intent)
        } else {
            // 로그인 필요 → 로그인 화면으로 이동
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        finish() // AutoLoginActivity 종료
    }
}