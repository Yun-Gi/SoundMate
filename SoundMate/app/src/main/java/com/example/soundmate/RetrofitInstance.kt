import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

data class User(
    val email: String,
    val gender: String,
    val age: Int
)

interface UserService {
    @POST("/api/signup")
    fun registerUser(@Body user: User): Call<Void>
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