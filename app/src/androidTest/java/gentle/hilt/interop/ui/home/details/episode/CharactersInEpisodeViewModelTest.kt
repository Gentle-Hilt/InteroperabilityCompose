package gentle.hilt.interop.ui.home.details.episode

import app.cash.turbine.test
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import gentle.hilt.interop.androidTestUtil.TestCoroutineRule
import gentle.hilt.interop.network.NetworkRepository
import gentle.hilt.interop.network.models.CharacterDetailsModel
import gentle.hilt.interop.network.models.EpisodeDetailsModel
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class CharactersInEpisodeViewModelTest {

    @get:Rule
    val hilt = HiltAndroidRule(this)

    @get:Rule
    val rule = TestCoroutineRule()


    @Inject
    lateinit var networkRepository: NetworkRepository

    lateinit var viewModel: CharactersInEpisodeViewModel

    @Before
    fun setup() {
        hilt.inject()
        MockKAnnotations.init(this)
        viewModel = CharactersInEpisodeViewModel(networkRepository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun fetchEpisodeDetails() = rule.runTest {
        val charactersResponse = listOf(CharacterDetailsModel(id = 1, name = "Rick"), CharacterDetailsModel(id = 2, name = "Morty"))
        val episodeResponse = EpisodeDetailsModel(id = 1)


        viewModel.fetchEpisodeDetails(1)

        advanceUntilIdle()
        viewModel.episodeState.test { Truth.assertThat(awaitItem()).isEqualTo(episodeResponse) }
        viewModel.charactersState.test { Truth.assertThat(awaitItem()).isEqualTo(charactersResponse) }

    }
}