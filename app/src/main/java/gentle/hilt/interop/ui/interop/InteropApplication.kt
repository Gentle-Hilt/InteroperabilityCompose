package gentle.hilt.interop.ui.interop

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import gentle.hilt.interop.data.datastore.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class InteropApplication : Application() {
    @Inject
    lateinit var dataStore: DataStoreManager
    override fun onCreate() {
        super.onCreate()

        Logger.clickableTimberLogs()

        runBlocking {
            withContext(Dispatchers.IO) {
                val nightModeEnabled = dataStore.darkModeEnabled.first()
                if (nightModeEnabled) {
                    Timber.d("nightMode enabled")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    Timber.d("nightMode disabled")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }
}
