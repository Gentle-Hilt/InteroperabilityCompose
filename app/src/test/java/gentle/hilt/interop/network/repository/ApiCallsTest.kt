package gentle.hilt.interop.network.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import gentle.hilt.interop.network.ApiClient
import gentle.hilt.interop.network.NetworkRepository
import gentle.hilt.interop.network.NetworkStatus
import gentle.hilt.interop.network.ResponseState
import gentle.hilt.interop.network.cache.Cache
import gentle.hilt.interop.localTestUtil.TestCoroutineRule
import gentle.hilt.interop.network.models.CharacterDetailsModel
import gentle.hilt.interop.network.models.CharactersPage
import gentle.hilt.interop.network.models.EpisodeDetailsModel
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class ApiCallsTest {
    @get:Rule
    val rule = TestCoroutineRule()

    @MockK
    lateinit var apiClient: ApiClient

    @RelaxedMockK
    private lateinit var context: Context

    private lateinit var networkRepository: NetworkRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        networkRepository = NetworkRepository(apiClient, context)
    }
    @After
    fun tearDown() {
        clearAllMocks()

        Cache.searchPage.clear()
        Cache.episode.clear()
        Cache.charactersInEpisode.clear()
    }

    @Test
    fun `getCharactersPage with successful response`() = rule.runTest {
        val mockResponse = mockk<Response<CharactersPage>>()
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns mockk()

        coEvery { apiClient.getCharactersPage(any()) } returns ResponseState.success(mockResponse)
        val result = networkRepository.getCharactersPage(1)

        assertThat(result).isNotNull()
    }

    @Test
    fun `getCharactersPage with failed response`() = rule.runTest {
        val mockResponse = mockk<Response<CharactersPage>>()
        every { mockResponse.isSuccessful } returns false
        every { mockResponse.code() } returns 404

        coEvery { apiClient.getCharactersPage(any()) } returns ResponseState.failure(Exception("404"))
        val result = networkRepository.getCharactersPage(1)

        assertThat(result).isNull()
    }
    @Test
    fun `getEpisode with successful response`() = rule.runTest {
        val cachedEpisode = EpisodeDetailsModel(1, "test")
        Cache.episode[1] = cachedEpisode

        val mockResponse = mockk<Response<EpisodeDetailsModel>>()
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns mockk()

        coEvery { apiClient.getEpisode(any()) } returns ResponseState.success(mockResponse)
        val result = networkRepository.getEpisode(1)

        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(cachedEpisode)
    }

    @Test
    fun `getEpisode with failed response`() = rule.runTest {
        val mockResponse = mockk<Response<EpisodeDetailsModel>>()
        every { mockResponse.isSuccessful } returns false
        every { mockResponse.code() } returns 404

        coEvery { apiClient.getEpisode(any()) } returns ResponseState.failure(Exception("404"))
        val result = networkRepository.getEpisode(1)

        assertThat(result).isNull()
    }


    @Test
    fun `getCharacters with successful response`() = rule.runTest {
        val cachedCharacters = listOf<CharacterDetailsModel>()
        Cache.charactersInEpisode[listOf()] = cachedCharacters

        val mockResponse = mockk<Response<List<CharacterDetailsModel>>>()
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns mockk()

        coEvery { apiClient.getCharacters(any()) } returns ResponseState.success(mockResponse)
        val result = networkRepository.getCharacters(listOf())

        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(cachedCharacters)
    }
    @Test
    fun `getCharacters with failed response`() = rule.runTest {
        val mockResponse = mockk<Response<List<CharacterDetailsModel>>>()
        every { mockResponse.isSuccessful } returns false
        every { mockResponse.code() } returns 404

        coEvery { apiClient.getCharacters(any()) } returns ResponseState.failure(Exception("404"))
        val result = networkRepository.getCharacters(emptyList())

        assertThat(result).isNull()
    }

    @Test
    fun `searchCharacterPage with successful response`() = rule.runTest {
        val pageIndex = 1
        val search = "Rick"
        val cachedSearch = mockk<CharactersPage>()
        Cache.searchPage["$search-$pageIndex"] = cachedSearch

        val mockResponse = mockk<Response<CharactersPage>>()
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns mockk()

        coEvery { apiClient.searchCharacterPage(any(), any()) } returns ResponseState.success(mockResponse)
        val result = networkRepository.searchCharacterPage(search, pageIndex)

        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(cachedSearch)
    }
    @Test
    fun `searchCharacterPage with failed response`() = rule.runTest {
        val mockResponse = mockk<Response<CharactersPage>>()
        every { mockResponse.isSuccessful } returns false
        every { mockResponse.code() } returns 404

        coEvery { apiClient.searchCharacterPage(any(), any()) } returns ResponseState.failure(Exception("404"))
        val result = networkRepository.searchCharacterPage("Rick", 1)

        assertThat(result).isNull()
    }


}