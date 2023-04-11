package gentle.hilt.interop

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class InteropApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DeleteInRelease.clickableTimberLogs()
    }
}
