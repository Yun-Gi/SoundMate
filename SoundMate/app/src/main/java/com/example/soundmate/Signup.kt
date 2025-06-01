package com.example.soundmate

import User
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import com.google.firebase.auth.FirebaseAuth


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
    var id by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var rpw by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


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

        Spacer(modifier = Modifier.height(36.dp))

        Text("회원가입", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("아이디와 비밀번호를 입력해주세요", fontSize = 14.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(8.dp))

        LoginTextField(label = " 이메일 ", value = id, onValueChange = { id = it}, withDivider = true)
        LoginTextField(label = " 비밀번호 ", value = pw, onValueChange = { pw = it }, password = true, withDivider = true, placeHolder = "비밀번호는 다음 중 3가지를 만족해야 합니다.")
        LoginTextField(label = " 비밀번호 확인 ", value = rpw, onValueChange = { rpw = it }, password = true, withDivider = true, placeHolder = "8자 이상, 대소문자, 숫자, 특수문자")

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
        LoginTextField(label = " 나이 ", value = age, onValueChange = { age = it }, withDivider = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onClick@ {
                if (id.isNotEmpty() && pw.isNotEmpty()) {
                    if (pw != rpw) {
                        AlertUtil.showCustomToast(context, "비밀번호가 일치하지 않습니다", R.drawable.remove_1828843)
                        return@onClick
                    }

                    if (!isValidPassword(pw)) {
                        AlertUtil.showCustomToast(context, "비밀번호는 다음 중 3가지를 만족해야 합니다:\n8자 이상, 대소문자, 숫자, 특수문자", R.drawable.remove_1828843)
                        return@onClick
                    }

                    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
                    if (!emailPattern.matches(id)) {
                        AlertUtil.showCustomToast(context, "올바른 이메일 형식을 입력해주세요", R.drawable.check_10302427)
                        return@onClick
                    }

                    firebaseAuth.createUserWithEmailAndPassword(id, pw)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = firebaseAuth.currentUser?.uid ?: ""
                                val user = User(email = id, gender = gender, age = age.toIntOrNull() ?: 0)

                                RetrofitInstance.api.registerUser(user).enqueue(object : retrofit2.Callback<Void> {
                                    override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                                        if (response.isSuccessful) {
                                            AlertUtil.showCustomToast(context, "회원가입 성공", R.drawable.check_10302427)
                                            val intent = Intent(context, Login::class.java)
                                            context.startActivity(intent)
                                        } else {
                                            AlertUtil.showCustomToast(context, "서버 저장 실패", R.drawable.remove_1828843)
                                        }
                                    }

                                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                                        AlertUtil.showCustomToast(context, "서버 연결 실패", R.drawable.remove_1828843)
                                    }
                                })
                            } else {
                                AlertUtil.showCustomToast(context, "회원가입 실패", R.drawable.remove_1828843)
                            }
                        }
                } else {
                    AlertUtil.showCustomToast(context, "이메일과 비밀번호를 입력해주세요", R.drawable.remove_1828843)
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
                    text = "이미 계정이 있으신가요?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF828282)
                )
                Text(
                    text = " 로그인하기",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF04B4AE),
                    modifier = Modifier.clickable {
                        val intent = Intent(context, Login::class.java)
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp)) // 텍스트와 구분선 사이 간격

            }
        }
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
}

fun isValidPassword(password: String): Boolean {
    var count = 0

    if (password.length >= 8) count++
    if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) count++
    if (password.any { it.isDigit() }) count++
    if (password.any { "!@#\$%^&*()_+-=[]|{};':\",./<>?".contains(it) }) count++

    return count >= 3
}