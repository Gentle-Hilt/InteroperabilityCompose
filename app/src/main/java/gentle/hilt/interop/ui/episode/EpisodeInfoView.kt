package gentle.hilt.interop.ui.episode

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import gentle.hilt.interop.network.models.EpisodeDetailsModel
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.gray
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.white
import gentle.hilt.interop.ui.settings.theme.robotoFontFamily
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

@Composable
fun BoxWithText(value: String) {
    val orientation = LocalConfiguration.current.orientation
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Box(
            Modifier
                .background(Color(gray), RoundedCornerShape(10.dp))
                .border(3.dp, Color.Gray, RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "$value ",
                    color = Color(white),
                    fontSize = 18.sp,
                    fontFamily = robotoFontFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    } else {
        Box(
            Modifier
                .background(Color(gray), RoundedCornerShape(10.dp))
                .border(3.dp, Color.Gray, RoundedCornerShape(10.dp))
                .padding(10.dp)

        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "$value ",
                    color = Color(white),
                    fontSize = 18.sp,
                    fontFamily = robotoFontFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentSize()
                )
            }
        }
    }
}

@Composable
fun EpisodeView(name: String, episode: String, created: String) {
    Box {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BoxWithText(value = "$episode: $name")
            Spacer(modifier = Modifier.height(5.dp))
            EpisodeCreationDate(created)
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
private fun EpisodeCreationDate(created: String) {
    if (created != "") {
        val date = Date.from(Instant.parse(created))
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val episodeCreated = dateFormat.format(date)
        BoxWithText(value = "Created: $episodeCreated")
    }
}

class EpisodeInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setViewTreeSavedStateRegistryOwner(findViewTreeSavedStateRegistryOwner())
    }

    var episodeInfo
        get() = episodeState.value
        set(value) {
            episodeState.value = value
        }

    private val episodeState = mutableStateOf(EpisodeDetailsModel())

    @Composable
    override fun Content() {
        EpisodeView(episodeInfo.name, episodeInfo.episode, episodeInfo.created)
    }
}
