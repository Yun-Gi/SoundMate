data class ConversationResponseDTO(
    val reply: String?,
    val recommend: Boolean,
    val emotion: String,
    val mood: String,
    val acousticness: Double,
    val danceability: Double,
    val energy: Double,
    val valence: Double,
    val speechiness: Double,
    val instrumentalness: Double,
    val liveness: Double,
    val tempo: Double,
    val recommendedSong: String?,
    val artist: String?,
    val youtubeUrl: String?
)
