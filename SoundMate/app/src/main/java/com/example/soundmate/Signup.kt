package com.example.soundmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import com.example.soundmate.ui.theme.SoundMateTheme

class Signup : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundMateTheme {
                SignupScreen()
            }
        }
    }
}

@Composable
fun SignupScreen() {
    val context = LocalContext.current
    var gender by remember { mutableStateOf("남성") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        Box(
            modifier = Modifier
                .size(41.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .border(BorderStroke(1.dp, Color(0xFFE8ECF4)), shape = RoundedCornerShape(12.dp))
                .clickable { (context as? ComponentActivity)?.finish() },
            contentAlignment = Alignment.Center
        ) {
            Text("<", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("회원가입", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Text("아이디와 비밀번호를 입력해주세요", fontSize = 14.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(24.dp))

        SignupTextField(label = "아이디", value = "cs2025")
        SignupTextField(label = "비밀번호", value = "1234", password = true)
        SignupTextField(label = "비밀번호 확인", value = "1234", password = true)
        SignupTextField(label = "이메일", value = "cs2025@gmail.com")

        Spacer(modifier = Modifier.height(8.dp))
        Text("성별", fontSize = 14.sp, color = Color(0xFF828282))
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            GenderButton("남성", gender == "남성") { gender = it }
            GenderButton("여성", gender == "여성") { gender = it }
        }

        Spacer(modifier = Modifier.height(16.dp))
        SignupTextField(label = "나이", value = "20")

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* 확인 동작 */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("확인", color = Color.White, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "이미 계정이 있으신가요? 로그인하기",
            fontSize = 15.sp,
            color = Color(0xFF1E232C),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SignupTextField(label: String, value: String, password: Boolean = false) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text(label, fontSize = 14.sp, color = Color(0xFF828282))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE0E0E0),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                disabledBorderColor = Color(0xFFE0E0E0)
            ),
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true
        )
    }
}

@Composable
fun GenderButton(label: String, selected: Boolean, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .width(54.dp)
            .height(32.dp)
            .background(
                if (selected) Color.Black else Color.White,
                shape = CircleShape
            )
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = Color(0xFFE6E6E6),
                shape = CircleShape
            )
            .clickable { onClick(label) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }