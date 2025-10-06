package com.example.soundmate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.soundmate.ui.theme.SoundMateTheme
import androidx.compose.foundation.border
import androidx.compose.ui.res.painterResource
import com.google.firebase.auth.FirebaseAuth

class PasswordRecovery : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundMateTheme {
                PasswordRecoveryScreen()
            }
        }
    }
}

@Composable
fun PasswordRecoveryScreen() {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.back), // 이미지 이름에 맞게 변경
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .size(41.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .border(BorderStroke(1.dp, Color(0xFFE8ECF4)), shape = RoundedCornerShape(12.dp))
                    .clickable { (context as? ComponentActivity)?.finish() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("비밀번호를 잃어버리셨나요?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("이메일을 입력 해 주세요", fontSize = 14.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(24.dp))

        LoginTextField(label = " 이메일", value = email, onValueChange = { email = it },withDivider = true)

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (email.isNotEmpty()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                AlertUtil.showCustomToast(context, "비밀번호 재설정 이메일을 전송했습니다", R.drawable.check_10302427)
                                // 비밀번호 재설정으로
                            } else {
                                AlertUtil.showCustomToast(context, "전송 실패", R.drawable.remove_1828843)
                            }
                        }
                } else {
                    AlertUtil.showCustomToast(context, "이메일을 입력해주세요", R.drawable.remove_1828843)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("확인", color = Color.White, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter // Box에서 아래 고정
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp), // 하단 여백
                horizontalArrangement = Arrangement.Center, // 가운데 정렬
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "비밀번호가 기억나셨나요? ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF828282)
                )
                Text(
                    text = "로그인하기",
                    fontSize = 14.sp,
                    color = Color(0xFF04B4AE),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        val intent = Intent(context, Signup::class.java)
                        context.startActivity(intent)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp)) // 텍스트와 구분선 사이 간격
            }
        }
    }
}

