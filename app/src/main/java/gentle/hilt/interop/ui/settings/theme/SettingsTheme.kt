package gentle.hilt.interop.ui.settings.theme

import android.annotation.SuppressLint
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import gentle.hilt.interop.data.datastore.DataStoreManager
import gentle.hilt.interop.ui.settings.theme.SettingsColors.Black
import gentle.hilt.interop.ui.settings.theme.SettingsColors.DarkGray
import gentle.hilt.interop.ui.settings.theme.SettingsColors.DarkGrayBright
import gentle.hilt.interop.ui.settings.theme.SettingsColors.FadeGray
import gentle.hilt.interop.ui.settings.theme.SettingsColors.Gray

@Composable
fun SettingsTheme(content: @Composable () -> Unit) {
    val dataStore = DataStoreManager(context = LocalContext.current)
    val isDarkModeSet = dataStore.darkModeEnabled.collectAsState(initial = false)
    MaterialTheme(
        colors = if (isDarkModeSet.value) {
            SettingsTheme.darkColors
        } else {
            SettingsTheme.lightColors
        }
    ) {
        content()
    }
}

object SettingsTheme {
    @SuppressLint("ConflictingOnColor")
    val lightColors: Colors = lightColors(
        primary = DarkGray,
        primaryVariant = DarkGray,
        secondary = DarkGrayBright,
        secondaryVariant = DarkGray,
        background = DarkGray,
        surface = DarkGray,
        error = DarkGray,
        onPrimary = FadeGray,
        onSecondary = FadeGray,
        onBackground = Gray,
        onSurface = FadeGray,
        onError = FadeGray
    )

    @SuppressLint("ConflictingOnColor")
    val darkColors: Colors = darkColors(
        primary = DarkGray,
        primaryVariant = DarkGray,
        secondary = DarkGrayBright,
        secondaryVariant = DarkGray,
        background = DarkGray,
        surface = DarkGray,
        error = DarkGray,
        onPrimary = FadeGray,
        onSecondary = FadeGray,
        onBackground = Black,
        onSurface = FadeGray,
        onError = FadeGray
    )
}
