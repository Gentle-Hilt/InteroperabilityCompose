package gentle.hilt.interop.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_details")
data class CharacterDetailsEntity(
    val characterIsFavorite: Boolean = false,
    @PrimaryKey
    val id: Int = 0,
    val characterImage: String = "",
    val characterName: String = "",
    val characterStatus: String = "",
    val characterGender: String = "",
    val characterLocation: String = "",
    val characterOrigin: String = "",
    val charactersInEpisode: List<String> = emptyList()
)
