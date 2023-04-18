package gentle.hilt.interop.ui.home.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import gentle.hilt.interop.network.NetworkRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {

    val networkState = repository.networkStatus.asLiveData(Dispatchers.IO)
}
