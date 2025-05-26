package com.example.soundmate

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import com.example.soundmate.ui.theme.SoundMateTheme
import androidx.compose.foundation.border
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundMateTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    var id by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "구글 로그인 성공", Toast.LENGTH_SHORT).show()
                        context.startActivity(Intent(context, ChatScreen::class.java))
                    } else {
                        Toast.makeText(context, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: ApiException) {
            Toast.makeText(context, "구글 계정 처리 오류: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

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
                    .border(BorderStroke(1.dp, Color(0xFFE8ECF4)), shape = RoundedCornerShape(12.dp))
                    .clickable { (context as? ComponentActivity)?.finish() },
                contentAlignment = Alignment.Center
            ) {
                Text("<", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("로그인", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("아이디와 비밀번호를 입력해주세요", fontSize = 14.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        LoginTextField(label = " 아이디", value = id,onValueChange = { id = it }, withDivider = true)
        LoginTextField(label = " 비밀번호",value = pw,onValueChange = { pw = it },password = true, withDivider = true)

        Row(
            modifier = Modifier
                .fillMaxWidth(), // Row가 전체 너비를 차지하지만
            horizontalArrangement = Arrangement.End, // 우측
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "비밀번호를 잊어버리셨나요?",
                fontSize = 14.sp,
                color = Color(0xFF1E232C),
                modifier = Modifier.clickable {
                    val intent = Intent(context, PasswordRecovery::class.java) // 비밀번호찾기로 변경
                    context.startActivity(intent)
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                if (id.isNotEmpty() && pw.isNotEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(id, pw)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                                // 로그인 성공 시 다음 화면으로 이동
                                val intent = Intent(context, ChatScreen::class.java)
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "로그인 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
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

        Spacer(modifier = Modifier.height(32.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Divider(
                color = Color(0xFFE6E6E6),
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f) // 남은 공간을 차지하게 만듦
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "다른 계정 연결",
                fontSize = 14.sp,
                color = Color(0xFF6A707C),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(12.dp)) // 텍스트와 구분선 사이 간격
            Divider(
                color = Color(0xFFE6E6E6),
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f) // 남은 공간을 차지하게 만듦
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        Button(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            },
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.google), // 여기에 이미지 파일명
                contentDescription = "Google Login" ,
                
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

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
                    text = "계정이 없으신가요?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF828282)
                )
                Text(
                    text = " 회원가입 하기",
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


@Composable
fun LoginTextField(label: String, value: String , onValueChange: (String) -> Unit, password: Boolean = false, withDivider: Boolean = false, placeHolder: String = "") {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(label, fontSize = 14.sp, color = Color(0xFF828282))
            if (withDivider) {
                Spacer(modifier = Modifier.width(8.dp))
                Divider(color = Color(0xFFE6E6E6), thickness = 1.dp, modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE0E0E0),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                disabledBorderColor = Color(0xFFE0E0E0)
            ),
            textStyle = LocalTextStyle.current.copy( // 텍스트 스타일 수정
                fontSize = 14.sp,  // 여기서 글자 크기를 조절
                lineHeight = 14.sp // 줄 간격을 조절하여 잘리지 않도록
            ),
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true,
            placeholder = {
                Text(
                    text = placeHolder,
                    fontSize = 13.sp,
                    lineHeight = 14.sp, // 줄 간격을 조절하여 잘리지 않도록
                    color = Color.LightGray
                )
            }
        )
    }
}


