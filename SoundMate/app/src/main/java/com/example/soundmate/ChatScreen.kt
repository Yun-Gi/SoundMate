package com.example.soundmate

import ConversationRequestDTO
import ConversationResponseDTO
import UserInfoDTO
import RetrofitClient
import ChatApiService
import ChatViewModel
import RecommendedSongDTO
import UserInfoResponse
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.soundmate.ui.theme.SoundMateTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

// 메시지 모델 클래스
data class ChatMessage(
    val isUser: Boolean,
    val text: String? = null,
    val imageUrl: String? = null,
    val recommendedSong: RecommendedSongDTO? = null
)

class ChatScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatViewModel: ChatViewModel by viewModels()

        setContent {
            SoundMateTheme {
                ChatScreenUI(viewModel = chatViewModel)
            }
        }
    }
}

@Composable
fun ChatScreenUI(viewModel: ChatViewModel) {
    var inputText by remember { mutableStateOf("") }
    val messages = viewModel.messages
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val userInfo = viewModel.userInfo

    LaunchedEffect(Unit) {
        if (userInfo == null) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
            Log.d("ChatScreenUI", "유저 uid: $uid")
            RetrofitClient.apiService.getUserInfo(uid).enqueue(object : Callback<UserInfoResponse> {
                override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                    Log.d("ChatScreenUI", "응답 성공: ${response.body()}")
                    Log.d("ChatScreenUI", "HTTP 응답코드: ${response.code()}")
                    Log.d("ChatScreenUI", "HTTP 바디 raw: ${response.raw()}")
                    Log.d("ChatScreenUI", "HTTP errorBody: ${response.errorBody()?.string()}")
                    response.body()?.let { user ->
                        viewModel.setUserInfo(
                            UserInfoDTO(
                                userId = uid,
                                gender = user.gender,
                                age = user.age,
                                genre = "ballad"
                            )
                        )
                    } ?: Log.d("ChatScreenUI", "응답은 왔는데 body()가 null임")
                }

                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                    Log.e("ChatScreenUI", "유저 정보 로드 실패: ${t.message}")
                }
            })
        }
    }

    // 무한 로딩 방지: userInfo 아직 없을 때만 로딩 표시
    if (userInfo == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }



    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // 채팅창에 이미지 먼저 표시
            viewModel.addMessage(ChatMessage(isUser = true, imageUrl = it.toString()))

            val inputStream = context.contentResolver.openInputStream(it)
            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
            inputStream?.use { input ->
                tempFile.outputStream().use { output -> input.copyTo(output) }
            }

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

            val gson = Gson()
            val userInfoJson = gson.toJson(userInfo)
            val userInfoPart = userInfoJson.toRequestBody("text/plain".toMediaTypeOrNull())

            RetrofitClient.apiService.analyzeImage(filePart, userInfoPart)
                .enqueue(object : Callback<ConversationResponseDTO> {
                    override fun onResponse(
                        call: Call<ConversationResponseDTO>,
                        response: Response<ConversationResponseDTO>
                    ) {
                        response.body()?.let { res ->
                            val dto = RecommendedSongDTO(
                                trackName = res.recommendedSong ?: "",
                                artistName = res.artist ?: "",
                                youtubeUrl = res.youtubeUrl
                            )
                            val replyMessage = buildString {
                                append(res.reply ?: "분석 결과를 받았습니다!")
                                if (res.recommendedSong != null && res.artist != null) {
                                    append("\n🎵 ${res.recommendedSong} - ${res.artist}")
                                }
                            }
                            viewModel.addMessage(ChatMessage(isUser = false, text = replyMessage, recommendedSong = dto))
                        }
                    }

                    override fun onFailure(call: Call<ConversationResponseDTO>, t: Throwable) {
                        viewModel.addMessage(
                            ChatMessage(
                                isUser = false,
                                text = "서버 오류: ${t.localizedMessage}"
                            )
                        )
                    }
                })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp)
            .background(Color.White)
    ) {
        // Header
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(15.dp))
            Image( //나중에 원형으로 마스킹
                painter = painterResource(id = R.drawable.lcon), // 이미지 이름에 맞게 변경
                contentDescription = "아이콘",
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White, shape = CircleShape)
                    .border(BorderStroke(1.dp, Color(0xFFE8ECF4)), shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "SoundMate",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.setting), // 이미지 이름에 맞게 변경
                contentDescription = "환경설정",
                modifier = Modifier
                    .size(65.dp)
                    .background(Color.White, shape = CircleShape)
                    .clickable {
                        val intent = Intent(context, Settings::class.java) // 비밀번호찾기로 변경
                        context.startActivity(intent)
                    }
            )
            Spacer(modifier = Modifier.width(5.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider(
            color = Color(0xFFE6E6E6),
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth() // ✅ 한 줄 전체 가로 너비 차지
        )
        Spacer(modifier = Modifier.height(8.dp))

        // LazyColumn 사용
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            items(messages.toList()) { message ->
                ChatBubble(
                    sender = message.isUser,
                    text = message.text,
                    imageUrl = message.imageUrl,
                    youtubeUrl = message.recommendedSong?.youtubeUrl,
                    recommendedSong = message.recommendedSong,
                    viewModel = viewModel
                )
            }
        }

        // Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("메시지 입력...") },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = "이미지 첨부")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB0B0B0),      // 더 진한 회색
                    unfocusedBorderColor = Color(0xFFB0B0B0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send // ⬅️ 키보드의 엔터 키를 "전송"으로 바꿈
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank()) {
                            viewModel.addMessage(ChatMessage(isUser = true, text = inputText))
                            val updatedMessages = messages.mapNotNull { it.text }

                            coroutineScope.launch {
                                scrollState.animateScrollToItem(messages.size - 1)
                            }

                            val request = ConversationRequestDTO(
                                messages = updatedMessages,
                                userInfo = userInfo
                            )

                            RetrofitClient.apiService.analyzeConversation(request)
                                .enqueue(object : Callback<ConversationResponseDTO> {
                                    override fun onResponse(
                                        call: Call<ConversationResponseDTO>,
                                        response: Response<ConversationResponseDTO>
                                    ) {
                                        response.body()?.let { res ->
                                            val dto = RecommendedSongDTO(
                                                trackName = res.recommendedSong ?: "",
                                                artistName = res.artist ?: "",
                                                youtubeUrl = res.youtubeUrl
                                            )
                                            val message = buildString {
                                                append(res.reply ?: "좋은 하루 되세요!")
                                                if (res.recommendedSong != null && res.artist != null) {
                                                    append("\n🎵 ${res.recommendedSong} - ${res.artist}")
                                                }
                                            }
                                            viewModel.addMessage(
                                                ChatMessage(
                                                    isUser = false,
                                                    text = message,
                                                    recommendedSong = dto
                                                )
                                            )
                                            coroutineScope.launch {
                                                scrollState.animateScrollToItem(messages.size - 1)
                                            }
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<ConversationResponseDTO>,
                                        t: Throwable
                                    ) {
                                        viewModel.addMessage(
                                            ChatMessage(
                                                isUser = false,
                                                text = "서버 오류: ${t.localizedMessage}"
                                            )
                                        )
                                        coroutineScope.launch {
                                            scrollState.animateScrollToItem(messages.size - 1)
                                        }
                                    }
                                })

                            inputText = ""
                        }
                    }
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
fun ChatBubble(sender: Boolean, text: String?, imageUrl: String?, youtubeUrl: String? = null, recommendedSong: RecommendedSongDTO? = null, viewModel: ChatViewModel) {
    val context = LocalContext.current

    fun getYoutubeThumbnailUrl(url: String?): String? {
        if (url == null) return null
        val regex = Regex("(?<=v=|be/|embed/)[^&?\\n]+")
        val videoId = regex.find(url)?.value
        return videoId?.let { "https://img.youtube.com/vi/$it/hqdefault.jpg" }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = if (sender) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .background(
                    if (sender) Color.Black else Color(0xFFE9E9EB),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            text?.let {
                Text(
                    text = it,
                    color = if (sender) Color.White else Color.Black,
                    fontSize = 14.sp
                )
            }
            imageUrl?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            youtubeUrl?.let { url ->
                val thumbUrl = getYoutubeThumbnailUrl(url)
                thumbUrl?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "YouTube 썸네일",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                    )
                }
            }

            // 리스트에 추가 버튼 (GPT가 추천한 경우만)
            if (!sender && recommendedSong != null && recommendedSong.trackName.isNotBlank()) {
                val context = LocalContext.current
                val userId = viewModel.userInfo?.userId

                if (userId != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            RetrofitClient.apiService.saveRecommendedSong(
                                userId = userId,
                                trackName = recommendedSong.trackName,
                                artistName = recommendedSong.artistName,
                                youtubeUrl = recommendedSong.youtubeUrl ?: ""
                            ).enqueue(object : Callback<Unit> {
                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                    Toast.makeText(context, "노래가 리스트에 추가되었어요!", Toast.LENGTH_SHORT).show()
                                }

                                override fun onFailure(call: Call<Unit>, t: Throwable) {
                                    Toast.makeText(context, "노래 추가에 실패했어요.", Toast.LENGTH_SHORT).show()
                                }
                            })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C))
                    ) {
                        Text(
                            text = "리스트에 추가",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
