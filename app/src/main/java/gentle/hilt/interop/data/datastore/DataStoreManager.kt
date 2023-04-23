package gentle.hilt.interop.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import gentle.hilt.interop.data.datastore.DataStoreManager.PreferencesKeys.LAST_CHARACTER_SEARCH
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore by preferencesDataStore("datastore")

class DataStoreManager @Inject constructor(
    @ApplicationContext appContext: Context
) {
    val dataStore = appContext.dataStore

    private object PreferencesKeys {
        val LAST_CHARACTER_SEARCH = stringPreferencesKey("LAST_CHARACTER_SEARCH")
    }
    suspend fun saveLastCharacterSearch(lastChSearch: String) = dataStore.edit { preferences ->
        preferences[LAST_CHARACTER_SEARCH] = lastChSearch
    }

    val readLastCharacterSearch: Flow<String> = dataStore.data.map { preferences ->
        preferences[LAST_CHARACTER_SEARCH] ?: ""
    }
}
