import RecommendedSongDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ChatApiService {

    @Multipart
    @POST("/api/chat/image")
    fun analyzeImage(
        @Part file: MultipartBody.Part,
        @Part("userInfo") userInfo: RequestBody
    ): Call<ConversationResponseDTO>

    @POST("/api/chat/conversation")
    fun analyzeConversation(@Body request: ConversationRequestDTO): Call<ConversationResponseDTO>

    // 추천곡 목록 조회
    @GET("/api/recommendations/{userId}")
    suspend fun getRecommendedSongs(@Path("userId") userId: String): Response<List<RecommendedSongDTO>>

    // 추천곡 삭제
    @DELETE("/api/recommendations/{userId}")
    suspend fun deleteRecommendedSong(
        @Path("userId") userId: String,
        @Query("trackName") trackName: String,
        @Query("artistName") artistName: String
    ): Response<Void>

    @POST("/api/recommendations/{userId}")
    fun saveRecommendedSong(
        @Path("userId") userId: String,
        @Query("trackName") trackName: String,
        @Query("artistName") artistName: String,
        @Query("youtubeUrl") youtubeUrl: String?
    ): Call<Unit>

    @GET("/api/user/info")
    fun getUserInfo(@Query("uid") uid: String): Call<UserInfoResponse>



}
