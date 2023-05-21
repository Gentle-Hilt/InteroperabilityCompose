package gentle.hilt.interop.network.client

import com.google.common.truth.Truth.assertThat
import gentle.hilt.interop.localTestUtil.TestCoroutineRule
import gentle.hilt.interop.network.ApiClient
import gentle.hilt.interop.network.ResponseState
import gentle.hilt.interop.network.models.CharacterDetailsModel
import gentle.hilt.interop.network.models.CharactersPage
import gentle.hilt.interop.network.models.EpisodeDetailsModel
import gentle.hilt.interop.network.models.PageInfo
import gentle.hilt.interop.network.service.ApiService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class ApiClientTest {
    @get:Rule
    val rule = TestCoroutineRule()

    @MockK
    lateinit var apiService: ApiService

    lateinit var apiClient: ApiClient

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        apiClient = ApiClient(apiService)
    }

    @Test
    fun `getEpisode should return success when api call is successful`() = rule.runTest {
        val expectedResponse = EpisodeDetailsModel(1, "1", "1", "1", emptyList(), "1", "1")
        val expectedSuccessfulResponse = Response.success(expectedResponse)

        coEvery { apiService.fetchEpisode(1) } returns expectedSuccessfulResponse
        val actualResponse = apiClient.getEpisode(1)

        assertThat(actualResponse).isEqualTo(ResponseState.success(expectedSuccessfulResponse))
    }

    @Test
    fun `getEpisode should return failure when api call is not successful`() = rule.runTest {
        val expectedErrorResponse = Response.error<EpisodeDetailsModel>(
            400,
            "Bad request".toResponseBody("application/json".toMediaTypeOrNull())
        )

        coEvery { apiService.fetchEpisode(1) } returns expectedErrorResponse
        val actualResponse = apiClient.getEpisode(1)

        val failure = ResponseState.failure<EpisodeDetailsModel>(Exception())::class.java
        assertThat(actualResponse).isInstanceOf(failure)
    }

    @Test
    fun `getEpisode should throw exception when api call throws exception`() = rule.runTest {
        val expectedMessage = "Network Error"

        coEvery { apiService.fetchEpisode(1) } throws Exception(expectedMessage)
        val actualResponse = apiClient.getEpisode(1)

        val failure = ResponseState.failure<EpisodeDetailsModel>(Exception())::class.java
        assertThat(actualResponse).isInstanceOf(failure)
        assertThat(actualResponse.exception?.message).contains(expectedMessage)
    }

    @Test
    fun `getCharacters should return success when api call is successful`() = rule.runTest {
        val expectedResponse = listOf(CharacterDetailsModel("1", listOf(), "Alive", 1, "Male"))
        val expectedSuccessfulResponse = Response.success(expectedResponse)

        coEvery { apiService.fetchMultipleCharacters(listOf("1")) } returns expectedSuccessfulResponse
        val actualResponse = apiClient.getCharacters(listOf("1"))

        assertThat(actualResponse).isEqualTo(ResponseState.success(expectedSuccessfulResponse))
    }

    @Test
    fun `getCharacters should return failure when api call is not successful`() = rule.runTest {
        val expectedErrorResponse = Response.error<List<CharacterDetailsModel>>(
            400,
            "Bad request".toResponseBody("application/json".toMediaTypeOrNull())
        )

        coEvery { apiService.fetchMultipleCharacters(listOf("1")) } returns expectedErrorResponse
        val actualResponse = apiClient.getCharacters(listOf("1"))

        val failure = ResponseState.failure<List<CharacterDetailsModel>>(Exception())::class.java
        assertThat(actualResponse).isInstanceOf(failure)
    }

    @Test
    fun `getCharacters should throw exception when api call throws exception`() = rule.runTest {
        val expectedMessage = "Network Error"

        coEvery { apiService.fetchMultipleCharacters(listOf("1")) } throws Exception(expectedMessage)
        val actualResponse = apiClient.getCharacters(listOf("1"))

        val failure = ResponseState.failure<List<CharacterDetailsModel>>(Exception())::class.java
        assertThat(actualResponse).isInstanceOf(failure)
        assertThat(actualResponse.exception?.message).contains(expectedMessage)
    }

    @Test
    fun `getCharactersPage should return success when api call is successful`() = rule.runTest {
        val pageIndex = 1
        val expectedResponse = CharactersPage(
            info = PageInfo(
                count = 826,
                pages = 42,
                next = "https://rickandmortyapi.com/api/character?page=2"
            )
        )
        val expectedSuccessfulResponse = Response.success(expectedResponse)

        coEvery { apiService.fetchCharactersPage(pageIndex) } returns expectedSuccessfulResponse
        val actualResponse = apiClient.getCharactersPage(pageIndex)

        assertThat(actualResponse).isEqualTo(ResponseState.success(expectedSuccessfulResponse))
    }

    @Test
    fun `getCharactersPage should return failure when api call is not successful`() = rule.runTest {
        val pageIndex = 1
        val expectedErrorResponse = Response.error<CharactersPage>(
            400,
            "Bad request".toResponseBody("application/json".toMediaTypeOrNull())
        )

        coEvery { apiService.fetchCharactersPage(pageIndex) } returns expectedErrorResponse
        val actualResponse = apiClient.getCharactersPage(pageIndex)

        val failure = ResponseState.failure<CharactersPage>(Exception())::class.java
        assertThat(actualResponse).isInstanceOf(failure)
    }

    @Test
    fun `getCharactersPage should throw exception when api call throws exception`() = rule.runTest {
        val pageIndex = 1
        val expectedMessage = "Network Error"

        coEvery { apiService.fetchCharactersPage(pageIndex) } throws Exception(expectedMessage)
        val actualResponse = apiClient.getCharactersPage(pageIndex)

        val failure = ResponseState.failure<CharactersPage>(Exception())::class.java
        assertThat(actualResponse).isInstanceOf(failure)
        assertThat(actualResponse.exception?.message).contains(expectedMessage)
    }

    @Test
    fun `searchCharacterPage should return success when api call is successful`() = rule.runTest {
        val expectedResponse = CharactersPage(
            info = PageInfo(
                count = 826,
                pages = 42,
                next = "https://rickandmortyapi.com/api/character?page=2"
            )
        )
        val expectedSuccessfulResponse = Response.success(expectedResponse)

        coEvery { apiService.searchCharacterPage("rick", 0) } returns expectedSuccessfulResponse
        val actualResponse = apiClient.searchCharacterPage("rick", 0)

        assertThat(actualResponse).isEqualTo(ResponseState.success(expectedSuccessfulResponse))
    }

    @Test
    fun `searchCharacterPage should return failure when api call is not successful`() = rule.runTest {
        val expectedErrorResponse = Response.error<CharactersPage>(
            400,
            "Bad request".toResponseBody("application/json".toMediaTypeOrNull())
        )

        coEvery { apiService.searchCharacterPage("rick", 0) } returns expectedErrorResponse
        val actualResponse = apiClient.searchCharacterPage("rick", 0)

        val failure = ResponseState.failure<CharactersPage>(Exception())::class.java
        assertThat(actualResponse).isInstanceOf(failure)
    }

    @Test
    fun `searchCharacterPage should throw exception when api call throws exception`() = rule.runTest {
        val expectedMessage = "Network Error"

        coEvery { apiService.searchCharacterPage("rick", 0) } throws Exception(expectedMessage)
        val actualResponse = apiClient.searchCharacterPage("rick", 0)

        val failure = ResponseState.failure<CharactersPage>(Exception())::class.java
        assertThat(actualResponse).isInstanceOf(failure)
        assertThat(actualResponse.exception?.message).contains(expectedMessage)
    }
}
