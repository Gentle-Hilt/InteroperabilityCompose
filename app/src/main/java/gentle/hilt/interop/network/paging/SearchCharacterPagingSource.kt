package gentle.hilt.interop.network.paging

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import gentle.hilt.interop.network.NetworkRepository
import gentle.hilt.interop.network.models.CharacterDetails
import javax.inject.Inject

class SearchCharacterPagingSource @Inject constructor(
    private val repository: NetworkRepository,
    private val userSearch: String
) : PagingSource<Int, CharacterDetails>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterDetails> {
        val pageNumber = params.key ?: 1
        val previousKey = if (pageNumber == 1) null else pageNumber - 1

        val request = repository.searchCharacterPage(userSearch, pageNumber)
            ?: return LoadResult.Error(Exception(repository.exceptionNetworkMessage))

        return LoadResult.Page(
            data = request.results,
            prevKey = previousKey,
            nextKey = getPageIndexFromNext(request.info.next)
        )
    }
    override fun getRefreshKey(state: PagingState<Int, CharacterDetails>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun getPageIndexFromNext(next: String?): Int? {
        if (next == null) {
            return null
        }
        val uri = Uri.parse(next)
        val page = uri.getQueryParameter("page") ?: return null
        return page.toInt()
    }
}