package gentle.hilt.interop.data.room.repository

import gentle.hilt.interop.data.room.dao.DaoCharacters
import gentle.hilt.interop.data.room.entities.CharacterDetailsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CharacterDetailsRepository @Inject constructor(
    private val dao: DaoCharacters
) {
    suspend fun insertCharacter(character: CharacterDetailsEntity) {
        dao.insertCharacter(character)
    }

    suspend fun deleteCharacter(character: CharacterDetailsEntity) {
        dao.deleteCharacter(character)
    }

    fun isCharacterFavorite(character: CharacterDetailsEntity): Flow<Boolean> {
        return dao.isCharacterFavorite(character.id)
    }

    fun observeCharacters(): Flow<List<CharacterDetailsEntity>> = dao.observeAllCharacters()
}
