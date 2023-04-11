package gentle.hilt.interop.network.models

import com.squareup.moshi.Json

data class EpisodeDetailsModel(
    val id: Int = 0,
    val name: String = "",
    @Json(name = "air_date") val airDate: String = "",
    val episode: String = "",
    val characters: List<String> = emptyList(),
    val url: String = "",
    val created: String = ""
)
