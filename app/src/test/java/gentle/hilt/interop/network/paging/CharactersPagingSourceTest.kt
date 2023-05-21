package gentle.hilt.interop.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.common.truth.Truth.assertThat
import gentle.hilt.interop.localTestUtil.TestCoroutineRule
import gentle.hilt.interop.network.NetworkRepository
import gentle.hilt.interop.network.models.CharacterDetailsModel
import gentle.hilt.interop.network.models.CharactersPage
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
    val rule = TestCoroutineRule()

    @MockK
    private lateinit var repository: NetworkRepository

    private lateinit var pagingSource: CharactersPagingSource

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        pagingSource = CharactersPagingSource(repository)
    }

    @Test
    fun `load() should return error when repository returns null`() = rule.runTest {
        val pageNumber = 1
        val loadParams = PagingSource.LoadParams.Refresh(pageNumber, 10, false)

        coEvery { repository.getCharactersPage(pageNumber) } returns null
        coEvery { repository.exceptionNetworkMessage } returns "Any error message for test"

        val result = pagingSource.load(loadParams)
        assertThat(result).isInstanceOf(PagingSource.LoadResult.Error::class.java)
    }

    @Test
    fun `load() should load page when repository returns correct response`() = runTest {
        val pageNumber = 1
        val pageRequest = CharactersPage(
            info = PageInfo(count = 10, pages = 2, next = "https://api.com/characters?page=2"),
            results = listOf(CharacterDetailsModel(id = 1, name = "Rick Sanchez"))
        )

        coEvery { repository.getCharactersPage(pageNumber) } returns pageRequest

        val loadParams = PagingSource.LoadParams.Refresh<Int>(null, 10, false)
        val result = pagingSource.load(loadParams)
        assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
        val pageResult = result as PagingSource.LoadResult.Page<Int, CharacterDetailsModel>
        assertThat(pageResult.data).isEqualTo(pageRequest.results)
        assertThat(pageResult.prevKey).isNull()
        assertThat(pageResult.nextKey).isEqualTo(2)
    }

    @Test
    fun `getRefreshKey() returns null when anchorPosition is Null`() {
        val pagingState = mockk<PagingState<Int, CharacterDetailsModel>>()

        every { pagingState.anchorPosition } returns null

        val result = pagingSource.getRefreshKey(pagingState)
        assertThat(result).isNull()
    }

    @Test
    fun `getRefreshKey() returns prevKey +1 when anchorPosition is Previous page`() {
        val pagingState = mockk<PagingState<Int, CharacterDetailsModel>>()
        val anchorPosition = 10

        every { pagingState.anchorPosition } returns anchorPosition
        every { pagingState.closestPageToPosition(anchorPosition) } returns mockk {
            every { prevKey } returns 1
        }

        val result = pagingSource.getRefreshKey(pagingState)
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `getRefreshKey() returns prevKey +1 when anchorPosition is Next page`() {
        val pagingState = mockk<PagingState<Int, CharacterDetailsModel>>()
        val anchorPosition = 10
        val page = mockk<PagingSource.LoadResult.Page<Int, CharacterDetailsModel>>()

        every { pagingState.anchorPosition } returns anchorPosition
        every { pagingState.closestPageToPosition(anchorPosition) } returns page
        every { page.nextKey } returns 3
        every { page.prevKey } returns 1

        val result = pagingSource.getRefreshKey(pagingState)
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `extractPageNumberFromLink() should return null when there's no link`() {
        val link: String? = null
        val result = pagingSource.extractPageNumberFromLink(link)
        assertThat(result).isNull()
    }

    @Test
    fun `extractPageNumberFromLink() should return null when provided with link without number`() {
        val linkWithoutNumber = "https://rickandmortyapi.com/api/character"

        val result = pagingSource.extractPageNumberFromLink(linkWithoutNumber)
        assertThat(result).isNull()
    }

    @Test
    fun `extractPageNumberFromLink() should return number from link`() {
        val correctLink = "https://rickandmortyapi.com/api/character?page=3"

        val result = pagingSource.extractPageNumberFromLink(correctLink)
        assertThat(result).isEqualTo(3)
    }
}
