package com.example.soundmate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.soundmate.ui.theme.SoundMateTheme

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
    ) {
        AsyncImage(
            model = "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/FeNV6EBnyd/5hg0qw43_expires_30_days.png",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedButton(
                onClick = { /* TODO: 뒤로가기 */ },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .height(36.dp)
            ) {
                AsyncImage(
                    model = "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/FeNV6EBnyd/119bajq9_expires_30_days.png",
                    contentDescription = null,
                    modifier = Modifier.size(19.dp)
                )
            }
            Text(
                "노래 리스트",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        songList.forEach { song ->
            SongItem(
                title = song.trackName,
                artist = song.artistName,
                url = song.youtubeUrl ?: "",
                onDelete = { viewModel.deleteSong(userId, song) }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
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