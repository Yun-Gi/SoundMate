import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class User(
    val email: String,
    val gender: String,
    val age: Int
)

data class UserInfoResponse(
    val gender: String,
    val age: Int
)

data class UserInfoRequest(
    val gender: String,
    val age: Int
)

interface UserService {
    @POST("/api/user/register")
    fun registerUser(@Body user: User): Call<Void>

    @GET("/api/user/info")
    fun getUserInfo(@Query("uid") uid: String): Call<UserInfoResponse>

    @PUT("/api/user/update/{id}")
    fun updateUserInfo(
        @Path("id") id: String,
        @Body userInfo: UserInfoRequest
    ): Call<Void>

}

object RetrofitInstance {
    private const val BASE_URL = "https://your-server-domain.com"

    val api: UserService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserService::class.java)
    }
}