package com.example.soundmate

import User
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.soundmate.ui.theme.SoundMateTheme

import androidx.compose.animation.core.animateDpAsState

import androidx.compose.foundation.gestures.detectHorizontalDragGestures

import androidx.compose.ui.input.pointer.pointerInput


class SongListSetting : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundMateTheme {
                SongListSettingScreen()
            }
        }
    }
}

@Composable
fun SongListSettingScreen(viewModel: SongListViewModel = viewModel()) {
    val context = LocalContext.current
    val songList by viewModel.songList.collectAsState()
    val scrollState = rememberScrollState()
    val userId = "user123"

    LaunchedEffect(Unit) {
        viewModel.fetchSongs(userId)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                    .border(
                        BorderStroke(1.dp, Color(0xFFE8ECF4)),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { (context as? ComponentActivity)?.finish() }
            )
            Spacer(modifier = Modifier.width(22.dp))
            Text("노래 리스트", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(32.dp))
        //테스트용
//        SwipeToRevealSongItem(
//            title = "노래 제목",
//            artist = "가수 이름",
//            url = "https://youtube.com/example",
//            onDelete = { /* 삭제 로직 */ }
//        )
        songList.forEach { song ->
            SwipeToRevealSongItem(
                title = song.trackName,
                artist = song.artistName,
                url = song.youtubeUrl ?: "",
                onDelete = { viewModel.deleteSong(userId, song) }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.BottomCenter // Box에서 아래 고정
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp), // 하단 여백
                horizontalArrangement = Arrangement.Center, // 가운데 정렬
                verticalAlignment = Alignment.CenterVertically
            ) {
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
}
@Composable
fun SongItem(title: String, artist: String, url: String, onDelete: () -> Unit) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        AsyncImage(
            model = "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/FeNV6EBnyd/2xjr8ae3_expires_30_days.png",
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(artist, fontSize = 12.sp)
            }

            if (url.isNotBlank()) {
                Text(
                    text = url.take(40) + if (url.length > 40) "..." else "",
                    fontSize = 10.sp,
                    color = Color(0xFF4285F4),
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )
            }
        }

        OutlinedButton(
            onClick = onDelete,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .height(36.dp)
        ) {
            Text("삭제", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SwipeToRevealSongItem(
    title: String,
    artist: String,
    url: String,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffsetX by animateDpAsState(targetValue = offsetX.dp, label = "offset")

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        // 유지 조건 설정
                        if (offsetX < -100f) {
                            offsetX = -100f
                        } else {
                            offsetX = 0f
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceIn(-200f, 0f)
                    }
                )
            }
    ) {
        // 뒷배경 (삭제 버튼)
        Row(
            modifier = Modifier
                .matchParentSize()
                .background(Color.White),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    onDelete()
                    offsetX = 0f
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .height(36.dp)
            ) {
                Text("삭제", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // 앞 콘텐츠
        Row(
            modifier = Modifier
                .offset(x = animatedOffsetX)
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            AsyncImage(
                model = "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/FeNV6EBnyd/2xjr8ae3_expires_30_days.png",
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(artist, fontSize = 12.sp)
                }

                if (url.isNotBlank()) {
                    Text(
                        text = url.take(40) + if (url.length > 40) "..." else "",
                        fontSize = 10.sp,
                        color = Color(0xFF4285F4),
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}