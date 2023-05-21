package gentle.hilt.interop.data.mappers

import com.google.common.truth.Truth.assertThat
import gentle.hilt.interop.data.room.entities.CharacterDetailsEntity
import gentle.hilt.interop.data.room.mappers.toEntity
import gentle.hilt.interop.data.room.mappers.toModel
import gentle.hilt.interop.network.models.CharacterDetailsModel
import org.junit.Test

class MappersTest {
    @Test
    fun `toEntity() converts CharacterDetailsModel to CharacterDetailsEntity`() {
        val model = CharacterDetailsModel(
            id = 1,
            image = "link",
            name = "Anime",
            status = "Dead Inside",
            gender = "Unknown",
            location = CharacterDetailsModel.Location("Never leave the house"),
            origin = CharacterDetailsModel.Origin("Unknown"),
            episode = listOf("Episode 1", "Episode 2")
        )

        val entity = model.toEntity()

        assertThat(model.id).isEqualTo(entity.id)
        assertThat(model.image).isEqualTo(entity.characterImage)
        assertThat(model.name).isEqualTo(entity.characterName)
        assertThat(model.status).isEqualTo(entity.characterStatus)
        assertThat(model.gender).isEqualTo(entity.characterGender)
        assertThat(model.location.name).isEqualTo(entity.characterLocation)
        assertThat(model.origin.name).isEqualTo(entity.characterOrigin)
        assertThat(model.episode).isEqualTo(entity.charactersInEpisode)
    }

    @Test
    fun `toModel() converts CharacterDetailsEntity to CharacterDetailsModel`() {
        val entity = CharacterDetailsEntity(
            id = 1,
            characterImage = "link",
            characterName = "Anime",
            characterStatus = "Dead Inside",
            characterGender = "Unknown",
            characterLocation = "Never leave the house",
            characterOrigin = "Unknown",
            charactersInEpisode = listOf("Episode 1", "Episode 2")
        )

        val model = entity.toModel()

        assertThat(entity.id).isEqualTo(model.id)
        assertThat(entity.characterImage).isEqualTo(model.image)
        assertThat(entity.characterName).isEqualTo( model.name)
        assertThat(entity.characterStatus).isEqualTo(model.status)
        assertThat(entity.characterGender).isEqualTo(model.gender)
        assertThat(entity.characterLocation).isEqualTo(model.location.name)
        assertThat(entity.characterOrigin).isEqualTo(model.origin.name)
        assertThat(entity.charactersInEpisode).isEqualTo(model.episode)
    }
}