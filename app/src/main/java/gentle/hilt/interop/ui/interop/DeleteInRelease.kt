package gentle.hilt.interop.ui.interop

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import timber.log.Timber

object DeleteInRelease {
    private const val TIMBER_WILL_JUMP_TO_MESSAGE_FROM_LOGCAT = 4

    fun clickableTimberLogs() {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy
            .newBuilder()
            .tag("")
            .methodCount(1)
            .methodOffset(TIMBER_WILL_JUMP_TO_MESSAGE_FROM_LOGCAT)
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                Logger.log(priority, "-$tag", message, t)
            }
        })
    }
}
