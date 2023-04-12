package gentle.hilt.interop.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import gentle.hilt.interop.network.NetworkRepository
import gentle.hilt.interop.network.paging.CharactersPagingSource
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val repository: NetworkRepository
) : ViewModel() {

    val pagedFlow = Pager(
        PagingConfig(pageSize = 35, prefetchDistance = 105, enablePlaceholders = false)
    ) {
        CharactersPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
}
