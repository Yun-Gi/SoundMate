import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ChatApiService {

    @Multipart
    @POST("/api/chat/image")
    fun analyzeImage(
        @Part file: MultipartBody.Part,
        @Part("userInfo") userInfo: RequestBody
    ): Call<ConversationResponseDTO>

    @POST("/api/chat/conversation")
    fun analyzeConversation(@Body request: ConversationRequestDTO): Call<ConversationResponseDTO>

}
