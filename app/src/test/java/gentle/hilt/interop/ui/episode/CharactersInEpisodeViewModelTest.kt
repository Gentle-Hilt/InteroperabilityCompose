package gentle.hilt.interop.ui.episode

import com.google.common.truth.Truth.assertThat
import gentle.hilt.interop.localTestUtil.TestCoroutineRule
import gentle.hilt.interop.network.ApiClient
import gentle.hilt.interop.network.NetworkRepository
import gentle.hilt.interop.network.NetworkStatus
import gentle.hilt.interop.network.ResponseState
import gentle.hilt.interop.network.cache.Cache
import gentle.hilt.interop.network.models.CharacterDetailsModel
import gentle.hilt.interop.network.models.EpisodeDetailsModel
import gentle.hilt.interop.network.service.ApiService
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
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
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class CharactersInEpisodeViewModelTest {

    @get:Rule
    val rule = TestCoroutineRule()

    @MockK
    lateinit var apiClient: ApiClient

    @MockK
    lateinit var networkRepository: NetworkRepository

    @MockK
    lateinit var apiService: ApiService

    private lateinit var viewModel: CharactersInEpisodeViewModel

    private val listOfCharacterDetailsModel = listOf(CharacterDetailsModel())
    private val episodeDetailsModel = EpisodeDetailsModel(
        // charactersInEpisode inside viewModel uses substringAfterLast to get number
        characters = listOf(
            "https://rickandmortyapi.com/api/character/1",
            "https://rickandmortyapi.com/api/character/2"
        )
    )

    // Not necessary to mockk ApiClient in viewModelTest.
    // Just for convenience in the future, i can easily check what type of data and where it goes
    // Without looking at it manually.
    private val charactersDetailsResponseState = ResponseState<List<CharacterDetailsModel>>(
        ResponseState.Status.Success,
        Response.success(listOfCharacterDetailsModel),
        null
    )
    private val episodeResponseState = ResponseState<EpisodeDetailsModel>(
        ResponseState.Status.Success,
        Response.success(episodeDetailsModel),
        null
    )

    private val charactersInEpisodeName = listOf("Rick", "Morty")
    private val charactersInEpisodeNumber = listOf("1", "2")
    private val episodeId = 1

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        coEvery { apiClient.getCharacters(charactersInEpisodeName) } returns charactersDetailsResponseState
        coEvery { apiClient.getEpisode(episodeId) } returns episodeResponseState

        coEvery { networkRepository.networkStatus } returns flowOf<NetworkStatus.Available>()
        coEvery { networkRepository.getCharacters(charactersInEpisodeNumber) } returns listOfCharacterDetailsModel
        coEvery { networkRepository.getEpisode(episodeId) } returns episodeDetailsModel

        viewModel = CharactersInEpisodeViewModel(networkRepository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        Cache.charactersInEpisode.clear()
        Cache.episode.clear()
    }

    @Test
    fun `fetchEpisodeDetails should emit values for episodeState and charactersState from Api`() = rule.runTest {
        viewModel.fetchEpisodeDetails(episodeId)

        advanceUntilIdle()
        assertThat(viewModel.episodeState.first()).isEqualTo(episodeDetailsModel)
        assertThat(viewModel.charactersState.first()).isEqualTo(listOfCharacterDetailsModel)
    }
}
