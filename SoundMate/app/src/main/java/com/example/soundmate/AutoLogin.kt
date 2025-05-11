package com.example.soundmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soundmate.ui.theme.SoundMateTheme
import androidx.compose.ui.text.font.FontWeight

class AutoLogin : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundMateTheme {
                AutoLoginScreen()
            }
        }
    }
}

@Composable
fun AutoLoginScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // 로고 이미지 (자동 로그인 로딩 화면)
        Image(
            painter = painterResource(id = R.drawable.logo), // 로고 파일 위치에 맞춰 수정 필요
            contentDescription = "앱 로고",
            modifier = Modifier
                .width(312.3.dp)
                .height(200.dp)
        )

        // 하단 텍스트 (앱 이름)
        Text(
            text = "SoundMate",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 58.dp)
        )
    }
}
