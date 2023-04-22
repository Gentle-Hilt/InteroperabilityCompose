package gentle.hilt.interop.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import gentle.hilt.interop.data.datastore.DataStoreManager.PreferencesKeys.MENU_STATE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore by preferencesDataStore("datastore")

class DataStoreManager @Inject constructor(
    @ApplicationContext appContext: Context
) {
    val dataStore = appContext.dataStore

    private object PreferencesKeys {
        val MENU_STATE = booleanPreferencesKey("MENU_STATE")
    }

    suspend fun saveMenuState(open: Boolean) = dataStore.edit { preferences ->
        preferences[MENU_STATE] = open
    }

    val readMenuState: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[MENU_STATE] ?: false
    }
}
