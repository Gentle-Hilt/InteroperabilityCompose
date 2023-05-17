package gentle.hilt.interop.localTestUtil

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent

/**

 *
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test/MIGRATION.md#replace-advancetimebyn-with-advancetimebyn-runcurrent
 */
@ExperimentalCoroutinesApi
@VisibleForTesting
fun TestScope.advanceTimeByAndRun(delayTimeMillis: Long) {
    testScheduler.advanceTimeBy(delayTimeMillis)
    runCurrent()
}
