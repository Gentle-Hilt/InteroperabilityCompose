package gentle.hilt.interop.data.room.mappers

import gentle.hilt.interop.data.room.entities.CharacterDetailsEntity
import gentle.hilt.interop.network.models.CharacterDetailsModel

fun CharacterDetailsModel.toEntity(): CharacterDetailsEntity {
    return CharacterDetailsEntity(
        id = this.id,
        characterImage = this.image,
        characterName = this.name,
        characterStatus = this.status,
        characterGender = this.gender,
        characterLocation = this.location.name,
        characterOrigin = this.origin.name,
        charactersInEpisode = this.episode
    )
}
