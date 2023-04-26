package gentle.hilt.interop.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gentle.hilt.interop.data.room.CharacterDataBase
import gentle.hilt.interop.data.room.dao.DaoCharacters
import gentle.hilt.interop.data.room.repository.CharacterDetailsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {
    @Provides
    @Singleton
    fun provideCharacterDataBase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        CharacterDataBase::class.java,
        "character_db"
    ).build()

    @Provides
    @Singleton
    fun provideCharacterDao(dataBase: CharacterDataBase) = dataBase.dao()

    @Provides
    @Singleton
    fun provideCharacterDetailsRepository(dao: DaoCharacters) = CharacterDetailsRepository(dao)
}
