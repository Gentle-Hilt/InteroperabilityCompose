package gentle.hilt.interop.network.paging


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.common.truth.Truth.assertThat
import gentle.hilt.interop.network.NetworkRepository
import gentle.hilt.interop.network.localTestUtil.TestCoroutineRule
import gentle.hilt.interop.network.models.CharacterDetails
import gentle.hilt.interop.network.models.GetCharactersPage
import gentle.hilt.interop.network.models.PageInfo
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class CharactersPagingSourceTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val rule = TestCoroutineRule()

    @MockK
    private lateinit var repository: NetworkRepository

    private lateinit var dataSource: CharactersPagingSource

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        dataSource = CharactersPagingSource(repository)
    }


    @Test
    fun `load returns LoadResult Error when repository returns null`() = rule.runTest {
        // Given
        val pageNumber = 1
        val loadParams = PagingSource.LoadParams.Refresh(pageNumber, 10, false)
        coEvery { repository.getCharactersPage(pageNumber) } returns null
        coEvery { repository.exceptionNetworkMessage } returns "Any error message for test"

        // When
        val result = dataSource.load(loadParams)

        // Then
        assertThat(result).isInstanceOf(PagingSource.LoadResult.Error::class.java)
    }

    @Test
    fun `load returns LoadResult Page when repository returns non-null response`() = runTest {
        // Given
        val pageNumber = 1
        val pageRequest = GetCharactersPage(
            info = PageInfo(count = 10, pages = 2, next = "https://api.com/characters?page=2"),
            results = listOf(CharacterDetails(id = 1, name = "Rick Sanchez"))
        )
        coEvery { repository.getCharactersPage(pageNumber) } returns pageRequest

        // When
        val loadParams = PagingSource.LoadParams.Refresh<Int>(null, 10, false)
        val result = dataSource.load(loadParams)

        // Then
        assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
        val pageResult = result as PagingSource.LoadResult.Page<Int, CharacterDetails>
        assertThat(pageResult.data).isEqualTo(pageRequest.results)
        assertThat(pageResult.prevKey).isNull()
        assertThat(pageResult.nextKey).isEqualTo(2)
    }

    @Test
    fun `getRefreshKey returns null when state anchorPosition is null`() {
        // Given
        val pagingState = mockk<PagingState<Int, CharacterDetails>>()
        every { pagingState.anchorPosition } returns null

        // When
        val result = dataSource.getRefreshKey(pagingState)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `getRefreshKey returns prevKey plus 1 when state anchorPosition is in previous page`() {
        // Given
        val pagingState = mockk<PagingState<Int, CharacterDetails>>()
        val anchorPosition = 10
        every { pagingState.anchorPosition } returns anchorPosition
        every { pagingState.closestPageToPosition(anchorPosition) } returns mockk {
            every { prevKey } returns 1
        }

        // When
        val result = dataSource.getRefreshKey(pagingState)

        // Then
        assertThat(result).isEqualTo(2)
    }


    @Test
    fun `getRefreshKey returns prevKey plus 1 when state anchorPosition is in next page`() {
        // Given
        val pagingState = mockk<PagingState<Int, CharacterDetails>>()
        val anchorPosition = 10

        val page = mockk<PagingSource.LoadResult.Page<Int, CharacterDetails>>()
        every { pagingState.anchorPosition } returns anchorPosition
        every { pagingState.closestPageToPosition(anchorPosition) } returns page
        every { page.nextKey } returns 3
        every { page.prevKey } returns 1

        // When
        val result = dataSource.getRefreshKey(pagingState)

        // Then
        assertThat(result).isEqualTo(2)
    }


    @Test
    fun `getPageIndexFromNext returns null when input is null`() {
        // Given
        val input: String? = null

        // When
        val result = dataSource.getPageIndexFromNext(input)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `getPageIndexFromNext returns null when input doesn't contain page query parameter`() {
        // Given
        val input = "https://rickandmortyapi.com/api/character"

        // When
        val result = dataSource.getPageIndexFromNext(input)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `getPageIndexFromNext returns the page query parameter value as an integer when input contains the page query parameter`() {
        // Given
        val input = "https://rickandmortyapi.com/api/character?page=3"

        // When
        val result = dataSource.getPageIndexFromNext(input)

        // Then
        assertThat(result).isEqualTo(3)
    }

}