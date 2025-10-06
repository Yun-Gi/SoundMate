import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.soundmate.ChatMessage
import UserInfoDTO
import android.util.Log

class ChatViewModel : ViewModel() {

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> get() = _messages

    private var _userInfo by mutableStateOf<UserInfoDTO?>(null)
    val userInfo: UserInfoDTO? get() = _userInfo

    fun addMessage(message: ChatMessage) {
        _messages.add(message)
    }

    fun clearMessages() {
        _messages.clear()
    }

    fun setUserInfo(info: UserInfoDTO) {
        Log.d("ChatViewModel", "setUserInfo 호출됨: $info")
        _userInfo = info
    }
}
