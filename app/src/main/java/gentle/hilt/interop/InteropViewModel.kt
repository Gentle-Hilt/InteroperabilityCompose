package gentle.hilt.interop

import android.view.View
import android.view.inputmethod.EditorInfo
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InteropViewModel @Inject constructor(
    private val repository: NetworkRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {
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
        dataStore.saveLastCharacterSearch(lastChSearch)
    }

    fun saveMenuSearchState(isExpanded: Boolean) = viewModelScope.launch {
        dataStore.saveSearchMenuState(isExpanded)
    }

    fun observeMenuSearchState(search: SearchMenuView, navController: NavController, searchView: SearchView) {
        viewModelScope.launch {
            dataStore.readSearchMenuState.distinctUntilChanged().collectLatest { isExpanded ->
                when (isExpanded) {
                    true -> {
                        searchView.imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN
                        searchView.isIconified = false
                        Timber.d("search menu expanded")
                        search.pagedData = pagingFlow
                        search.navController = navController
                        search.visibility = View.VISIBLE
                        search.dataStore = dataStore

                        dataStore.readLastCharacterSearch.distinctUntilChanged().collectLatest { lastCharacter ->
                            if (lastCharacter.isNotEmpty()) {
                                Timber.d("previously submitted name $lastCharacter")
                                searchView.setQuery(lastCharacter, false)
                            }
                        }
                    }
                    false -> {
                        Timber.d("search menu collapsed")
                        searchView.onActionViewCollapsed()
                        search.visibility = View.GONE
                    }
                }
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
        query: String
    ): Flow<PagingData<CharacterDetails>> {
        searchQuery = query
        pagingSource?.invalidate()
        return pagingFlow
    }
}
