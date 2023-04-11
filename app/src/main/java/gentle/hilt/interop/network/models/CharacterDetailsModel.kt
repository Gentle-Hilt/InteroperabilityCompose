package gentle.hilt.interop.network.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharacterDetailsModel(
    val created: String = "",
    val episode: List<String> = listOf(),
    val gender: String = "",
    val id: Int = 0,
    val image: String = "",
    val location: Location = Location(),
    val name: String = "",
    val origin: Origin = Origin(),
    val species: String = "",
    val status: String = "",
    val type: String = "",
    val url: String = ""
) : Parcelable {
    @Parcelize
    data class Location(
        val name: String = "",
        val url: String = ""
    ) : Parcelable

    @Parcelize
    data class Origin(
        val name: String = "",
        val url: String = ""
    ) : Parcelable
}
