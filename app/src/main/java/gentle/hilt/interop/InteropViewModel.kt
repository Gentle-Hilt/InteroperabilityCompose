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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
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
    val isSearchExpanded = MutableStateFlow(false)

    fun saveIsSearchExpandedState(isExpanded: Boolean) {
        isSearchExpanded.value = isExpanded
    }
   /* private val pagingSource = SearchCharacterPagingSource(repository, searchQuery)
    private val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 40
        ),
        pagingSourceFactory = { pagingSource }
    ).flow.cachedIn(viewModelScope)*/

    /*val pagedState2 = MutableStateFlow(pagingFlow)*/

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
            .cachedIn(viewModelScope)
        pagedState.value = paging

        search.pagedData = pagedState.value
        search.clearFocus = searchView
        search.hideKeyboard = searchListener
        search.navController = navController
        return paging
    }

    fun savePagedDataInRotation(search: SearchMenuView) {
        viewModelScope.launch {
            isSearchExpanded.collectLatest { isExpanded ->
                Timber.d("$isExpanded from viewModel")
                when (isExpanded) {
                    true -> {
                        search.pagedData = pagedState.value
                        search.visibility = View.VISIBLE
                    }

                    false -> {
                        search.visibility = View.GONE
                    }
                }
            }
        }
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

    fun saveLastCharacterSearch(lastChSearch: String) = viewModelScope.launch {
        Timber.d("saving name:$lastChSearch")
        dataStore.saveLastCharacterSearch(lastChSearch)
    }

    @OptIn(FlowPreview::class)
    fun setPreviouslySearchedCharacter(searchView: SearchView) {
        viewModelScope.launch {
            dataStore.readLastCharacterSearch.debounce(50).collectLatest { lastCharacter ->
                Timber.d("read name: $lastCharacter")
                searchView.setQuery(lastCharacter, false)
            }
        }
    }

    // Creating paging source to not create it each time when user types something
    // In device rotation it should not blink now, because we're reusing existing PagingSource
    private var searchQuery = ""
    private var pagingSource: SearchCharacterPagingSource? = null

    init {
        pagingSource = getPagingSource()
    }
    private fun getPagingSource(): SearchCharacterPagingSource {
        if (pagingSource == null || pagingSource!!.invalid) {
            pagingSource = SearchCharacterPagingSource(repository, searchQuery)
        }
        return pagingSource!!
    }

    val pagingFlow: Flow<PagingData<CharacterDetails>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 40
            ),
            pagingSourceFactory = { getPagingSource() }
        ).flow.cachedIn(viewModelScope)

    fun updatedSearch(
        query: String,
        search: SearchMenuView,
        navController: NavController,
        searchListener: SearchView.OnQueryTextListener,
        searchView: SearchView
    ): Flow<PagingData<CharacterDetails>> {
        searchQuery = query

        search.clearFocus = searchView
        search.hideKeyboard = searchListener
        search.navController = navController
        search.pagedData = pagingFlow
        pagingSource?.invalidate()
        return pagingFlow
    }
}
