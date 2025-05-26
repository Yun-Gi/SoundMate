package com.example.soundmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape




class Settings : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundMateTheme {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var gender by remember { mutableStateOf("남성") }
    var pw by remember { mutableStateOf("") }
    var cpw by remember { mutableStateOf("") }
    var rcpw by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(41.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .border(
                        BorderStroke(1.dp, Color(0xFFE8ECF4)),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { (context as? ComponentActivity)?.finish() },
                contentAlignment = Alignment.Center
            ) {
                Text("<", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text("환경설정", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        LoginTextField(label = " 비밀번호 변경", value = pw, onValueChange = { pw = it}, withDivider = true, placeHolder = "현재 비밀번호 입력")
        LoginTextField(label = " 새로운 비밀번호 입력 ", value = cpw, onValueChange = { pw = it }, password = true, withDivider = true)
        LoginTextField(label = " 새로운 비밀번호 확인 ", value = rcpw, onValueChange = { rcpw = it }, password = true, withDivider = true)

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "  성별 ",
                fontSize = 14.sp,
                color = Color(0xFF828282)
            )

            Spacer(modifier = Modifier.width(8.dp)) // 텍스트와 구분선 사이 간격

            Divider(
                color = Color(0xFFE6E6E6),
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f) // 남은 공간을 차지하게 만듦
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {
            GenderButton("남성", gender == "남성") { gender = it }
            GenderButton("여성", gender == "여성") { gender = it }
        }

        Spacer(modifier = Modifier.height(8.dp))
        LoginTextField(label = " 나이 ", value = age, onValueChange = { age = it }, withDivider = true)

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "  선호 노래 장르 설정 ",
                fontSize = 14.sp,
                color = Color(0xFF828282)
            )

            Spacer(modifier = Modifier.width(8.dp)) // 텍스트와 구분선 사이 간격

            Divider(
                color = Color(0xFFE6E6E6),
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f) // 남은 공간을 차지하게 만듦
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(54.dp)
                    .height(32.dp)
                    .background(
                        Color.Black,
                        shape = CircleShape
                    )
                    .border(
                        width = 0.dp,
                        color = Color(0xFFE6E6E6),
                        shape = CircleShape
                    )
                    .clickable {  },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "확인",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "  노래 리스트 설정 ",
                fontSize = 14.sp,
                color = Color(0xFF828282)
            )

            Spacer(modifier = Modifier.width(8.dp)) // 텍스트와 구분선 사이 간격

            Divider(
                color = Color(0xFFE6E6E6),
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f) // 남은 공간을 차지하게 만듦
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(54.dp)
                    .height(32.dp)
                    .background(
                        Color.Black,
                        shape = CircleShape
                    )
                    .border(
                        width =  0.dp,
                        color = Color(0xFFE6E6E6),
                        shape = CircleShape
                    )
                    .clickable {  },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "확인",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("확인", color = Color.White, fontSize = 14.sp)
        }

    }
}

