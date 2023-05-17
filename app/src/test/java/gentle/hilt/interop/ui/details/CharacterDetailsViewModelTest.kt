package gentle.hilt.interop.ui.details

import gentle.hilt.interop.data.room.repository.CharacterDetailsRepository
import gentle.hilt.interop.localTestUtil.TestCoroutineRule
import gentle.hilt.interop.network.NetworkRepository
import gentle.hilt.interop.network.cache.Cache
import gentle.hilt.interop.ui.home.details.CharacterDetailsViewModel
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.*
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
        viewModel = CharacterDetailsViewModel(networkRepository, chRepository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        Cache.charactersInEpisode.clear()
        Cache.episode.clear()
    }

    @Test
    fun `yes`() = rule.runTest {
    }

}