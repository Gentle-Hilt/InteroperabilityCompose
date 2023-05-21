package gentle.hilt.interop.ui.details

import com.google.common.truth.Truth.assertThat
import gentle.hilt.interop.data.room.entities.CharacterDetailsEntity
import gentle.hilt.interop.data.room.repository.CharacterDetailsRepository
import gentle.hilt.interop.localTestUtil.TestCoroutineRule
import gentle.hilt.interop.localTestUtil.advanceTimeByAndRun
import gentle.hilt.interop.network.NetworkRepository
import gentle.hilt.interop.network.NetworkStatus
import gentle.hilt.interop.network.cache.Cache
import gentle.hilt.interop.ui.home.details.CharacterDetailsViewModel
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class CharacterDetailsViewModelTest {

    @get:Rule
    val rule = TestCoroutineRule()

    @MockK
    lateinit var networkRepository: NetworkRepository

    @MockK
    lateinit var chRepository: CharacterDetailsRepository

    private lateinit var viewModel: CharacterDetailsViewModel
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        coEvery { networkRepository.networkStatus } returns flowOf<NetworkStatus.Available>()
        viewModel = CharacterDetailsViewModel(networkRepository, chRepository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        Cache.charactersInEpisode.clear()
        Cache.episode.clear()
    }

    private val entity = CharacterDetailsEntity()

    @Test
    fun `isCharacterFavorite() should return boolean of favorite character`() = rule.runTest {
        coEvery { viewModel.isCharacterFavorite(entity) } returns flowOf(entity.characterIsFavorite)

        val result = viewModel.isCharacterFavorite(entity)
        assertThat(result.first()).isEqualTo(entity.characterIsFavorite)
    }

    @Test
    fun `addCharacterAsFavorite() should add new character`() = rule.runTest{
        viewModel.addCharacterAsFavorite(entity)

        advanceUntilIdle()
        coVerify { chRepository.insertCharacter(entity) }
    }

    @Test
    fun `deleteCharacter() should delete character`() = rule.runTest {
        viewModel.addCharacterAsFavorite(entity)
        advanceTimeByAndRun(100)
        coVerify { chRepository.insertCharacter(entity) }

        viewModel.deleteCharacterFromFavorite(entity)
        advanceUntilIdle()
        coVerify { chRepository.deleteCharacter(entity) }

    }

}