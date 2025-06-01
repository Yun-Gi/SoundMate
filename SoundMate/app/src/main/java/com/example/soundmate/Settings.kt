package com.example.soundmate

import UserInfoRequest
import UserInfoResponse
import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import com.google.firebase.auth.EmailAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
    val scrollState = rememberScrollState()
    var gender by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var cpw by remember { mutableStateOf("") }
    var rcpw by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    // 기존 유저 설정 띄우기 용 유저정보 가져와야 함
    val firebaseAuth = FirebaseAuth.getInstance()
    val user = firebaseAuth.currentUser
    val userId = user?.uid ?: ""

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            RetrofitInstance.api.getUserInfo(userId).enqueue(object : retrofit2.Callback<UserInfoResponse> {
                override fun onResponse(call: Call<UserInfoResponse>, response: retrofit2.Response<UserInfoResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            gender = it.gender
                            age = it.age.toString()
                        }
                    } else {
                        AlertUtil.showCustomToast(context, "유저 정보 불러오기 실패", R.drawable.remove_1828843)
                    }
                }

                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                    AlertUtil.showCustomToast(context, "서버 연결 오류", R.drawable.remove_1828843)
                }
            })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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

        Text("환경설정", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        LoginTextField(label = " 비밀번호 변경", value = pw, onValueChange = { pw = it}, withDivider = true, placeHolder = "현재 비밀번호 입력")
        LoginTextField(label = " 새로운 비밀번호 입력 ", value = cpw, onValueChange = { pw = it }, password = true, withDivider = true, placeHolder = "비밀번호는 다음 중 3가지를 만족해야 합니다.")
        LoginTextField(label = " 새로운 비밀번호 확인 ", value = rcpw, onValueChange = { rcpw = it }, password = true, withDivider = true, placeHolder = "8자 이상, 대소문자, 숫자, 특수문자")

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
                    .clickable {
                        val intent = Intent(context, SongListSetting::class.java)
                        context.startActivity(intent)
                    },
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

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "  저장 하기 ",
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
        Button(
            onClick = {
                if (pw.isNotEmpty()) {
                    val credential = EmailAuthProvider.getCredential(user?.email ?: "", pw)
                    user?.reauthenticate(credential)?.addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            if (cpw == rcpw) {
                                if (!isValidPassword(cpw)) {
                                    AlertUtil.showCustomToast(context, "비밀번호는 다음 중 3가지를 만족해야 합니다:\n8자 이상, 대소문자, 숫자, 특수문자", R.drawable.remove_1828843)
                                    return@addOnCompleteListener
                                }
                                user.updatePassword(cpw).addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        AlertUtil.showCustomToast(context, "비밀번호가 변경되었습니다", R.drawable.check_10302427)

                                        // 비밀번호 변경 후 유저 정보 업데이트
                                        val request = UserInfoRequest(gender = gender, age = age.toIntOrNull() ?: 0)
                                        RetrofitInstance.api.updateUserInfo(user.uid, request)
                                            .enqueue(object : Callback<Void> {
                                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                                    if (response.isSuccessful) {
                                                        AlertUtil.showCustomToast(context, "정보가 저장되었습니다", R.drawable.check_10302427)
                                                    } else {
                                                        AlertUtil.showCustomToast(context, "정보 저장 실패", R.drawable.remove_1828843)
                                                    }
                                                }

                                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                                    AlertUtil.showCustomToast(context, "서버 연결 실패", R.drawable.remove_1828843)
                                                }
                                            })

                                    } else {
                                        AlertUtil.showCustomToast(context, "비밀번호 변경 실패", R.drawable.remove_1828843)
                                    }
                                }
                            } else {
                                AlertUtil.showCustomToast(context, "새 비밀번호가 일치하지 않습니다", R.drawable.remove_1828843)
                            }
                        } else {
                            AlertUtil.showCustomToast(context, "현재 비밀번호가 일치하지 않습니다", R.drawable.remove_1828843)
                        }
                    }
                } else {
                    // 비밀번호 변경 없이 정보만 저장
                    val request = UserInfoRequest(gender = gender, age = age.toIntOrNull() ?: 0)
                    RetrofitInstance.api.updateUserInfo(userId, request)
                        .enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    AlertUtil.showCustomToast(context, "정보가 저장되었습니다", R.drawable.check_10302427)
                                } else {
                                    AlertUtil.showCustomToast(context, "정보 저장 실패", R.drawable.remove_1828843)
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                AlertUtil.showCustomToast(context, "서버 연결 실패", R.drawable.remove_1828843)
                            }
                        })
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

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "  로그아웃 하기 ",
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
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut() // 로그아웃 처리

                AlertUtil.showCustomToast(context, "로그아웃 되었습니다", R.drawable.check_10302427)

                // 로그인 화면으로 이동
                val intent = Intent(context, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 뒤로가기 방지
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFEEEEEE))
        ) {
            Text("로그아웃", color = Color.Black, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

