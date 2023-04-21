package gentle.hilt.interop

import android.widget.SearchView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import gentle.hilt.interop.network.NetworkRepository
import gentle.hilt.interop.network.models.CharacterDetails
import gentle.hilt.interop.network.paging.SearchCharacterPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class InteropViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {
    fun search(
        query: String,
        search: SearchMenuView,
        navController: NavController,
        searchListener: SearchView.OnQueryTextListener,
        searchView: SearchView
    ): Flow<PagingData<CharacterDetails>> {
        val paging = Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { SearchCharacterPagingSource(repository, query) }
        ).flow.cachedIn(viewModelScope)

        search.clearFocus = searchView
        search.hideKeyboard = searchListener
        search.navController = navController
        search.pagedData = paging
        return paging
    }

}
