package com.example.soundmate

import RecommendedSongDTO
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import android.util.Log

class SongListViewModel : ViewModel() {

    private val _songList = MutableStateFlow<List<RecommendedSongDTO>>(emptyList())
    val songList: StateFlow<List<RecommendedSongDTO>> = _songList

    fun fetchSongs(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getRecommendedSongs(userId)
                if (response.isSuccessful) {
                    _songList.value = response.body() ?: emptyList()
                } else {
                    Log.e("SongListViewModel", "fetchSongs 실패: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("SongListViewModel", "fetchSongs 예외 발생", e)
            }
        }
    }

    fun deleteSong(userId: String, song: RecommendedSongDTO) {
        viewModelScope.launch {
            try {
                val response: Response<Void> = RetrofitClient.apiService
                    .deleteRecommendedSong(userId, song.trackName, song.artistName)

                if (response.isSuccessful) {
                    fetchSongs(userId) // 삭제 후 목록 새로고침
                } else {
                    Log.e("SongListViewModel", "delete 실패: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("SongListViewModel", "deleteSong 예외 발생", e)
            }
        }
    }
}
