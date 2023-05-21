package gentle.hilt.interop.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import gentle.hilt.interop.data.room.CharacterDataBase
import gentle.hilt.interop.data.room.dao.DaoCharacters
import gentle.hilt.interop.data.room.entities.CharacterDetailsEntity
import gentle.hilt.interop.localTestUtil.TestCoroutineRule
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class DaoCharactersTest {

    @get:Rule
    val rule = TestCoroutineRule()

    private lateinit var dataBase: CharacterDataBase
    private lateinit var daoCharacters: DaoCharacters

    private lateinit var observeAllCharacters: List<CharacterDetailsEntity>
    @Before
    fun setup() {
        MockKAnnotations.init(this)

        val context = ApplicationProvider.getApplicationContext<Context>()
        dataBase = Room.inMemoryDatabaseBuilder(context, CharacterDataBase::class.java)
            .allowMainThreadQueries()
            .build()

        daoCharacters = dataBase.dao()

        runTest {
            daoCharacters.insertCharacter(character)
            observeAllCharacters = daoCharacters.observeAllCharacters().first()
        }
    }

    @After
    fun tearDown() {
        dataBase.close()

    }

    private val character = CharacterDetailsEntity(id = 1)

    @Test
    fun `insertCharacter() should add to db`() = rule.runTest {
        assertThat(observeAllCharacters).isNotEmpty()
    }

    @Test
    fun `deleteCharacter() should delete from db`() = rule.runTest {
        daoCharacters.deleteCharacter(character)

        observeAllCharacters = daoCharacters.observeAllCharacters().first()
        assertThat(observeAllCharacters).isEmpty()
    }

    @Test
    fun `isCharacterFavorite() should return favorite ch by id`() = rule.runTest {
        daoCharacters.isCharacterFavorite(character.id)
        advanceUntilIdle()
        assertThat(observeAllCharacters.first().characterIsFavorite).isEqualTo(character.characterIsFavorite)

    }

}
