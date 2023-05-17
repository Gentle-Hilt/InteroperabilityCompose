package gentle.hilt.interop.network.service


import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import gentle.hilt.interop.localTestUtil.TestCoroutineRule
import gentle.hilt.interop.network.models.CharacterDetailsModel
import gentle.hilt.interop.network.models.CharactersPage
import gentle.hilt.interop.network.models.EpisodeDetailsModel
import gentle.hilt.interop.network.models.PageInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@Config(application = HiltTestApplication::class, manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
class ApiServiceTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val rule = TestCoroutineRule()

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var moshi: Moshi

    lateinit var apiServiceException: ApiService

    @Before
    fun setUp() {
        hiltRule.inject()

        apiServiceException = Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/404/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }


    @Test
    fun `fetchEpisode should provide expected response when server responds successfully`() = rule.runTest {
        val episodeId = 1
        val expectedResponse = EpisodeDetailsModel(
            id = 1,
            name = "Pilot",
        )

        val fetchedData = apiService.fetchEpisode(episodeId)

        assertThat(fetchedData.isSuccessful).isTrue()
        assertThat(fetchedData.body()?.name).isEqualTo(expectedResponse.name)
        assertThat(fetchedData.body()?.id).isEqualTo(expectedResponse.id)
    }

    @Test
    fun `fetchEpisode should return error when server is responding with error`() = rule.runTest {
        val episodeId = -1

        val result = apiService.fetchEpisode(episodeId)

        assertThat(result.isSuccessful).isFalse()
        assertThat(result.code()).isEqualTo(404)
    }

    @Test
    fun `fetchEpisode should return an error response when an exception is thrown`() = rule.runTest {
        val episodeId = 1

        val result = apiServiceException.fetchEpisode(episodeId)

        assertThat(result.isSuccessful).isFalse()
        assertThat(result.errorBody()).isNotNull()
    }


    @Test
    fun `fetchCharactersPage should provide next chapter when server responds successfully`() = rule.runTest {
        val pageIndex = 1
        val expectedResponse = CharactersPage(
            info = PageInfo(count = 826, pages = 42, next = "https://rickandmortyapi.com/api/character?page=2")
        )

        val fetchedData = apiService.fetchCharactersPage(pageIndex)

        assertThat(fetchedData.isSuccessful).isTrue()
        assertThat(fetchedData.body()?.info).isEqualTo(expectedResponse.info)
    }

    @Test
    fun `fetchCharactersPage should return error when server is responding with error`() = rule.runTest {
        val pageIndex = 999999

        val result = apiService.fetchCharactersPage(pageIndex)

        assertThat(result.isSuccessful).isFalse()
        assertThat(result.errorBody()).isNotNull()
    }

    @Test
    fun `fetchCharactersPage should return an error response when an exception is thrown`() = rule.runTest {
        val pageIndex = 1

        val result = apiServiceException.fetchCharactersPage(pageIndex)

        assertThat(result.isSuccessful).isFalse()
        assertThat(result.errorBody()).isNotNull()
    }


    @Test
    fun `fetchMultipleCharacter should return expected result when server responds successfully`() = rule.runTest {
        val listOf = listOf("1", "2", "3")
        val expectedResponse = listOf(
            CharacterDetailsModel(
                id = 1,
                name = "Rick Sanchez"
            ),
            CharacterDetailsModel(
                id = 2,
                name = "Morty Smith"
            ),
            CharacterDetailsModel(
                id = 3,
                name = "Summer Smith"
            )
        )

        val result = apiService.fetchMultipleCharacters(listOf)


        assertThat(result.isSuccessful).isTrue()
        assertThat(result.body()?.size).isEqualTo(expectedResponse.size)

        for (i in expectedResponse.indices) {
            assertThat(result.body()!![i].id).isEqualTo(expectedResponse[i].id)
            assertThat(result.body()!![i].name).isEqualTo(expectedResponse[i].name)
        }
    }

    @Test
    fun `fetchMultipleCharacters should return error when api responds with an error`() = rule.runTest {
        val result = apiService.fetchMultipleCharacters(emptyList())

        assertThat(result.isSuccessful).isFalse()
        assertThat(result.errorBody()).isNotNull()
    }

    @Test
    fun `fetchMultipleCharacters should return exception when API throws exception`() = rule.runTest {
        val listOf = listOf("1", "2", "3")
        val expectedResponse = listOf(
            CharacterDetailsModel(
                id = 1,
                name = "Rick Sanchez"
            ),
            CharacterDetailsModel(
                id = 2,
                name = "Morty Smith"
            ),
            CharacterDetailsModel(
                id = 3,
                name = "Summer Smith"
            )
        )

        val result = apiServiceException.fetchMultipleCharacters(listOf)

        assertThat(result.isSuccessful).isFalse()
        assertThat(result.body()?.size).isNotEqualTo(expectedResponse.size)
    }


    @Test
    fun `searchCharacterPage should provide searched chapter when server responds successfully`() = rule.runTest {
        val characterName = "Rick"
        val pageIndex = 1
        val expectedResponse = CharactersPage(
            info = PageInfo(
                count = 107,
                pages = 6,
                next = "https://rickandmortyapi.com/api/character?page=2&name=Rick",
                prev = null
            )
        )

        val result = apiService.searchCharacterPage(characterName, pageIndex)

        assertThat(result.isSuccessful).isTrue()
        assertThat(result.body()?.info).isEqualTo(expectedResponse.info)
    }


    @Test
    fun `searchCharacterPage should return error when api responds with error`() = rule.runTest{
        val characterName = "Null"
        val pageIndex = 1
        val result = apiService.searchCharacterPage(characterName, pageIndex)

        assertThat(result.isSuccessful).isFalse()
        assertThat(result.errorBody()).isNotNull()
    }

    @Test
    fun `searchCharacterPage should return exception when API throws exception`() = rule.runTest {
        val characterName = "Rick"
        val pageIndex = 1
        val expectedResponse = CharactersPage(
            info = PageInfo(
                count = 107,
                pages = 6,
                next = "https://rickandmortyapi.com/api/character?page=2&name=Rick",
                prev = null
            )
        )

        val result = apiServiceException.searchCharacterPage(characterName, pageIndex)

        assertThat(result.isSuccessful).isFalse()
        assertThat(result.body()?.info).isNotEqualTo(expectedResponse.info)
    }
}