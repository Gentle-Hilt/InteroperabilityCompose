package gentle.hilt.interop.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gentle.hilt.interop.data.datastore.DataStoreManager
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val dataStore: DataStoreManager
) : ViewModel()
