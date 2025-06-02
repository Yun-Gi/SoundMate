package com.example.soundmate

import RecommendedSongDTO
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class SongListViewModel : ViewModel() {

    private val _songList = MutableStateFlow<List<RecommendedSongDTO>>(emptyList())
    val songList: StateFlow<List<RecommendedSongDTO>> = _songList

    fun fetchSongs(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getRecommendedSongs(userId)
                _songList.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteSong(userId: String, song: RecommendedSongDTO) {
        viewModelScope.launch {
            try {
                val response: Response<Void> = RetrofitClient.apiService
                    .deleteRecommendedSong(userId, song.trackName, song.artistName)

                if (response.isSuccessful) {
                    fetchSongs(userId)
                } else {
                    println("삭제 실패: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
