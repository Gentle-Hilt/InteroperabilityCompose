package gentle.hilt.interop.network.paging

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import gentle.hilt.interop.network.NetworkRepository
import gentle.hilt.interop.network.models.CharacterDetailsModel
import javax.inject.Inject

class SearchCharacterPagingSource @Inject constructor(
    private val repository: NetworkRepository,
    private var userSearch: String
) : PagingSource<Int, CharacterDetailsModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterDetailsModel> {
        val pageNumber = params.key ?: 1
        val previousKey = if (pageNumber == 1) null else pageNumber - 1

        val characterPage = repository.searchCharacterPage(userSearch, pageNumber)
            ?: return LoadResult.Error(Exception(repository.exceptionNetworkMessage))

        return LoadResult.Page(
            data = characterPage.results,
            prevKey = previousKey,
            nextKey = extractPageNumberFromLink(characterPage.info.next)
        )
    }
    override fun getRefreshKey(state: PagingState<Int, CharacterDetailsModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    fun extractPageNumberFromLink(next: String?): Int? {
        if (next == null) {
            return null
        }
        val uri = Uri.parse(next)
        val page = uri.getQueryParameter("page") ?: return null
        return page.toInt()
    }
}
