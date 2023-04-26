package gentle.hilt.interop.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import gentle.hilt.interop.data.room.entities.CharacterDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoCharacters {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterDetailsEntity)

    @Delete
    suspend fun deleteCharacter(character: CharacterDetailsEntity)

    @Query("SELECT * FROM character_details")
    fun observeAllCharacters(): Flow<List<CharacterDetailsEntity>>
}
