package gentle.hilt.interop

import android.view.View
import android.widget.SearchView
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import gentle.hilt.interop.data.datastore.DataStoreManager
import gentle.hilt.interop.network.NetworkRepository
import gentle.hilt.interop.network.models.CharacterDetails
import gentle.hilt.interop.network.paging.SearchCharacterPagingSource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InteropViewModel @Inject constructor(
    private val repository: NetworkRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {
    private val emptyPagedData = PagingData.empty<CharacterDetails>()
    private val emptyFlowPagedData: Flow<PagingData<CharacterDetails>> = flow {
        emit(emptyPagedData)
    }
    val pagedState = MutableStateFlow(emptyFlowPagedData)

    @OptIn(FlowPreview::class)
    fun search(
        query: String,
        search: SearchMenuView,
        navController: NavController,
        searchListener: SearchView.OnQueryTextListener,
        searchView: SearchView
    ): Flow<PagingData<CharacterDetails>> {
        val paging = Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 40
            ),
            pagingSourceFactory = { SearchCharacterPagingSource(repository, query) }
        ).flow
            .debounce(500)
            .cachedIn(viewModelScope)
        pagedState.value = paging

        search.pagedData = pagedState.value
        search.clearFocus = searchView
        search.hideKeyboard = searchListener
        search.navController = navController
        return paging
    }

    @OptIn(FlowPreview::class)
    fun loadingState(loading: ComposeView) {
        viewModelScope.launch {
            repository.isLoading.debounce(300).collect { isLoading ->
                when (isLoading) {
                    true -> loading.visibility = View.VISIBLE
                    false -> loading.visibility = View.GONE
                }
            }
        }
    }

    fun saveMenuState(open: Boolean) = viewModelScope.launch {
        dataStore.saveMenuState(open)
    }
    val readMenuState = dataStore.readMenuState
}
