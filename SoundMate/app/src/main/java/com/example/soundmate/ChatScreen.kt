package com.example.soundmate

import ConversationRequestDTO
import ConversationResponseDTO
import UserInfoDTO
import RetrofitClient
import ChatApiService
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.soundmate.ui.theme.SoundMateTheme
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
    val imageUrl: String? = null
)

class ChatScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundMateTheme {
                ChatScreenUI()
            }
        }
    }
}

@Composable
fun ChatScreenUI() {
    var inputText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val userInfo = UserInfoDTO("female", 23, "ballad")

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // 채팅창에 이미지 먼저 표시
            messages.add(ChatMessage(isUser = true, imageUrl = it.toString()))

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
                            val replyMessage = buildString {
                                append(res.reply ?: "분석 결과를 받았습니다!")
                                if (res.recommendedSong != null && res.artist != null) {
                                    append("\n🎵 ${res.recommendedSong} - ${res.artist}")
                                }
                            }
                            messages.add(ChatMessage(isUser = false, text = replyMessage))
                        }
                    }

                    override fun onFailure(call: Call<ConversationResponseDTO>, t: Throwable) {
                        messages.add(ChatMessage(isUser = false, text = "서버 오류: ${t.localizedMessage}"))
                    }
                })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SoundMate",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // LazyColumn 사용
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            items(messages.toList()) { message ->
                ChatBubble(sender = message.isUser, text = message.text, imageUrl = message.imageUrl)
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
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("메시지 입력") },
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                Icon(imageVector = Icons.Default.Image, contentDescription = "이미지 첨부")
            }

            Spacer(modifier = Modifier.width(8.dp))
            val userInfo = UserInfoDTO("female", 23, "ballad")

            Button(onClick = {
                if (inputText.isNotBlank()) {
                    messages.add(ChatMessage(isUser = true, text = inputText))
                    val updatedMessages = messages.mapNotNull { it.text }

                    coroutineScope.launch {
                        scrollState.animateScrollToItem(messages.size - 1)
                    }

                    val request = ConversationRequestDTO(
                        messages = updatedMessages,
                        userInfo = userInfo
                    )

                    RetrofitClient.apiService.analyzeConversation(request)
                        .enqueue(object : retrofit2.Callback<ConversationResponseDTO> {
                            override fun onResponse(
                                call: Call<ConversationResponseDTO>,
                                response: retrofit2.Response<ConversationResponseDTO>
                            ) {
                                response.body()?.let { res ->
                                    val message = buildString {
                                        append(res.reply ?: "좋은 하루 되세요!")
                                        if (res.recommendedSong != null && res.artist != null) {
                                            append("\n🎵 ${res.recommendedSong} - ${res.artist}")
                                        }
                                    }
                                    messages.add(ChatMessage(isUser = false, text = message))
                                    coroutineScope.launch {
                                        scrollState.animateScrollToItem(messages.size - 1)
                                    }
                                }
                            }

                            override fun onFailure(call: Call<ConversationResponseDTO>, t: Throwable) {
                                messages.add(ChatMessage(isUser = false, text = "서버 오류: ${t.localizedMessage}"))
                                coroutineScope.launch {
                                    scrollState.animateScrollToItem(messages.size - 1)
                                }
                            }
                        })

                    inputText = ""
                }
            }) {
                Text("전송")
            }
        }
    }
}

@Composable
fun ChatBubble(sender: Boolean, text: String?, imageUrl: String?) {
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
        }
    }
}
