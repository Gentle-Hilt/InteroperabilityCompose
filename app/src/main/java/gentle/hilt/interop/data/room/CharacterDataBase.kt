package gentle.hilt.interop.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gentle.hilt.interop.data.room.CharacterDataBase.Companion.DB_CHARACTERS_VERSION
import gentle.hilt.interop.data.room.converters.Converters
import gentle.hilt.interop.data.room.dao.DaoCharacters
import gentle.hilt.interop.data.room.entities.CharacterDetailsEntity

@Database(
    entities = [CharacterDetailsEntity::class],
    version = DB_CHARACTERS_VERSION
)
@TypeConverters(Converters::class)
abstract class CharacterDataBase : RoomDatabase() {
    abstract fun dao(): DaoCharacters

    companion object {
        const val DB_CHARACTERS_VERSION = 1
    }
}
