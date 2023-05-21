package gentle.hilt.interop.data.repository

import gentle.hilt.interop.data.room.dao.DaoCharacters
import gentle.hilt.interop.data.room.entities.CharacterDetailsEntity
import gentle.hilt.interop.data.room.repository.CharacterDetailsRepository
import gentle.hilt.interop.localTestUtil.TestCoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class CharacterDetailsRepositoryTest{
    @get:Rule
    val rule = TestCoroutineRule()

    @MockK
    lateinit var dao: DaoCharacters

    private lateinit var repository: CharacterDetailsRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repository = CharacterDetailsRepository(dao)

        coEvery { dao.insertCharacter(character) } returns Unit
        coEvery { dao.deleteCharacter(character) } returns Unit

        runTest {
            repository.insertCharacter(character)
        }
    }

    @After
    fun tearDown() {

    }
    private val character = CharacterDetailsEntity(id = 4)

    @Test
    fun `insertCharacter() should call insert dao`() = rule.runTest {
        coVerify { dao.insertCharacter(character) }
    }

    @Test
    fun `deleteCharacter() should call delete dao`() = rule.runTest{
        repository.deleteCharacter(character)

        coVerify { dao.insertCharacter(character) }
        coVerify { dao.deleteCharacter(character) }
    }

    @Test
    fun `isCharacterFavorite() should call isCharacterFavorite dao`() = rule.runTest{
        coEvery { dao.isCharacterFavorite(character.id) } returns flowOf()
        repository.isCharacterFavorite(character)

        coVerify { dao.isCharacterFavorite(character.id) }
    }

    @Test
    fun `observeAllCharacters should call observeAllCharacters dao `() = rule.runTest {
        coEvery { dao.observeAllCharacters() } returns (flowOf(listOf(character)))

        repository.observeCharacters()
        coVerify { dao.observeAllCharacters() }
    }
}