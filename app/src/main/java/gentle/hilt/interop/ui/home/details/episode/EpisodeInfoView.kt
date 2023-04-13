package gentle.hilt.interop.ui.home.details.episode

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gentle.hilt.interop.network.models.EpisodeDetails
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.gray
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.white

@Composable
fun TextWithBackground(value: String) {
    Box(
        Modifier
            .background(Color(gray), RoundedCornerShape(8.dp))
            .wrapContentSize()
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text("$value ", color = Color(white), fontSize = 18.sp)
        }
    }
}

@Composable
fun EpisodeView(name: String, episode: String, about: String = "Characters in episode") {
    Box(contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                TextWithBackground(value = "Name")
                Spacer(modifier = Modifier.width(3.dp))
                TextWithBackground(value = name)
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row {
                TextWithBackground(value = episode)
                Spacer(modifier = Modifier.width(3.dp))
                TextWithBackground(value = about)
            }
        }
    }
}
class EpisodeInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    var episodeInfo
        get() = episodeState.value
        set(value) {
            episodeState.value = value
        }

    private val episodeState = mutableStateOf(EpisodeDetails())

    @Composable
    override fun Content() {
        EpisodeView(episodeInfo.name, episodeInfo.episode)
    }
}
