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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
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
fun SongListSettingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("노래 리스트", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        SongItem("Drowning", "WOODZ", "https://www.youtube.com/watch?v=Nb")
        SongItem("모르시나요", "조째즈", "https://www.youtube.com/watch?v=tfmwbxBKPh0")
        SongItem("미치게 그리워서", "황가람", "https://www.youtube.com/watch?v=EZq-SAnSizw")
        SongItem("Chosen", "Blxst", "https://www.youtube.com/watch?v=qFSb1MabqyE")
        SongItem("Teeth", "5 Seconds of Summer", "https://www.youtube.com/watch?v=JWeJHN5P-E8")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* 확인 동작 */ },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("확인", color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
fun SongItem(title: String, artist: String, url: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(artist, fontSize = 12.sp, color = Color.Gray)
            Text(url, fontSize = 10.sp, color = Color(0xFF4285F4))
        }

        Button(
            onClick = { /* 삭제 동작 */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("삭제", color = Color.White, fontSize = 14.sp)
        }
    }
}