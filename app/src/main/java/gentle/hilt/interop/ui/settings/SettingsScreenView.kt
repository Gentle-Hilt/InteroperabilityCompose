package gentle.hilt.interop.ui.settings

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gentle.hilt.interop.data.datastore.DataStoreManager
import gentle.hilt.interop.ui.robotoFontFamily
import gentle.hilt.interop.ui.settings.theme.SettingsTheme
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val dataStore = DataStoreManager(context = LocalContext.current)
    val isDarkModeSet = dataStore.darkModeEnabled.collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, MaterialTheme.colors.onBackground, RoundedCornerShape(3.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Dark Mode",
                    fontSize = 35.sp,
                    fontFamily = robotoFontFamily,
                    color = MaterialTheme.colors.onBackground
                )
                Spacer(Modifier.width(16.dp))
            }
            Switch(
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colors.secondary,
                    uncheckedThumbColor = MaterialTheme.colors.onSurface
                ),
                checked = isDarkModeSet.value,
                onCheckedChange = { isChecked ->
                    coroutineScope.launch {
                        dataStore.setDarkMode(isChecked)
                    }
                },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            )
        }
    }
}

class SettingsScreenView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    @Composable
    override fun Content() {
        SettingsTheme {
            SettingsScreen()
        }
    }
}
