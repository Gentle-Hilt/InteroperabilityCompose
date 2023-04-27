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

fun CharacterDetailsEntity.toModel(): CharacterDetailsModel {
    return CharacterDetailsModel(
        id = this.id,
        image = this.characterImage,
        name = this.characterName,
        status = this.characterStatus,
        gender = this.characterGender,
        location = CharacterDetailsModel.Location(this.characterLocation),
        origin = CharacterDetailsModel.Origin(this.characterOrigin),
        episode = this.charactersInEpisode
    )
}
