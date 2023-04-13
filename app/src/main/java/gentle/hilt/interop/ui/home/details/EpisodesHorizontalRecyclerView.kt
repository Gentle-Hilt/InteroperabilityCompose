package gentle.hilt.interop.ui.home.details

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.findNavController
import coil.compose.rememberAsyncImagePainter
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.gray

@Composable
fun Episode(episode: String, navController: NavController) {
    val episodeNumber = episode.substringAfterLast("/").toInt()
    val action = CharacterDetailsFragmentDirections.actionCharacterDetailsFragmentToCharactersInEpisodeFragment(episodeNumber)
    val orientation = LocalContext.current.resources.configuration.orientation

    var height = 250.dp
    var width = 250.dp

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        height = 150.dp
        width = 150.dp
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        backgroundColor = Color.DarkGray,
        modifier = Modifier
            .height(height)
            .width(width)
            .padding(8.dp)
            .clickable { navController.navigate(action) }
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(ImageLinks.links.getOrNull(episodeNumber)),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize()

            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(Color(gray))
            ) {
                Text(
                    text = "Episode: $episodeNumber",
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalRecyclerView(episodes: List<String>, navController: NavController) {
    LazyRow(flingBehavior = rememberSnapFlingBehavior(rememberLazyListState())) {
        items(episodes.size) { index ->
            Episode(episodes[index], navController)
        }
    }
}

class EpisodesHorizontalRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    var episodes: List<String>
        get() = episodesState
        set(value) {
            episodesState.clear()
            episodesState.addAll(value)
        }

    private val episodesState = mutableListOf<String>()
    private val navController by lazy { findNavController() }

    @Composable
    override fun Content() {
        HorizontalRecyclerView(episodes = episodesState, navController)
    }
}

object ImageLinks {
    val links = listOf(
        "https://m.media-amazon.com/images/M/MV5BNzlhNGI0MTUtOWZlNS00ZmQ2LTk2NTYtMGMwMzRmOGViZWIyXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BNzUyNmIxYzYtMTM2My00ZTZjLWFmODYtNmFmMDgyZDY4OWU4XkEyXkFqcGdeQXVyNjg0Nzk2Nzc@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BM2I0Nzg0YTktYTc2Zi00NTk4LWI5ZDQtMmVhYjBjZmRmNGM0XkEyXkFqcGdeQXVyNjg4ODczODM@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BZWZmZDBjYTEtZTQyNy00NDc5LTgxMjEtZmQyNmU1N2Y1NThkXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BNGQyOWNhYzQtNTJhNy00NzFhLTkyYTgtYTM4MGI0NmYxMWJiXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BYjg2NjhlMWQtMjgxOS00YTVlLWJjN2UtZjhmMjA2ZjMzYmMzXkEyXkFqcGdeQXVyMTMxMDE2OTU2._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BYmRlZTdmNTEtNmY5Ny00MTlmLTk5M2UtNGQwYzFkYzU2YWRlXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BNzM4MmM3MjctYjE0Yy00NzVhLThkNmQtNjBhNWU2MDc1ZjNhXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BNDIzNDEwOGQtNTE5Mi00ZWM0LWJmYTQtZTk0ZTAzM2Q2ZWZkXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BZWRhYzZhZjMtMjQxMi00OWVkLWEzNjItMjE3ZmZjM2QwZWU2L2ltYWdlXkEyXkFqcGdeQXVyNDk3OTIwNTU@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BOGU0ODA0ZDYtNWJiYy00MGM0LThhZDAtMDkxNjZhMGRhZWEyXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BM2E5NWMwMDItYTJmNi00NmYzLWI3YjItNzllOWU1OWRiZDA5XkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BZGJiYzFlMjMtZjIyYi00YzhkLWIwMjUtZmUyNWUyMDMxMjZhXkEyXkFqcGdeQXVyMjQyODEzMjc@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BOTExYzYxODYtOGRiMi00MzBmLTkwNzMtY2Q5ZDU2ZjdmOGZmXkEyXkFqcGdeQXVyNTkyMjE3NDU@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BOWFjNzJhMjYtNTFlMi00MThlLWE4MDItYjg0ODYxZWZkNzZjXkEyXkFqcGdeQXVyNTkyMjE3NDU@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BYjc5N2ZhZjEtNmY2Ny00YjU4LTg5MTMtYzIyOGNiNjc0YTE1XkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BZjMwZmM5N2EtMDUyYi00NmM0LTlkYTItYWExOTY3NTUxYTY2XkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BNTAyMjg5MTAtODRjNi00MGYxLWJjZWItYTU5MGQ2NmY4Yjc1XkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BZGFlNTlkYjctYTYxNS00YWE4LTk1ZTYtNTcyNWY0ZTQ1MzhhXkEyXkFqcGdeQXVyNjgyOTUyNDc@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BNWZlMmQ0ZjEtZGQ4Yi00YTcxLWJjOGQtZmJmYmRkYmE3NTc3XkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BNmVjM2IzOTQtNzZmNi00MzFjLTg3MGUtYTIwNzk3MDBhMWEwXkEyXkFqcGdeQXVyNTkyMjE3NDU@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BY2FkMGEzYTQtZDczZC00OWYzLTk1M2ItN2YxNzk0NjdjNWU1XkEyXkFqcGdeQXVyNjAxNDAxNDc@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BMGE2ODY0M2ItYWRhZC00ZjEzLTkyNWUtYWQ1MDUzZGE3NTQ5XkEyXkFqcGdeQXVyNjk2NTI2MDI@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BN2EzM2JlMDMtMmUzNC00ZTY4LThhNzItZjIyNzBiYzYzOGZkXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BMGRlMGQ3MDMtNDgzNi00MWI1LTk0ZTgtNzRiMWYyYWRhNGEyXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BNTIyYWY4Y2UtYTNiOS00NDMzLWE1ZmEtNjJjYTRlYzIwMWY2XkEyXkFqcGdeQXVyODA0MTgwMTY@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BMDA0OGI2OTktMjEwOS00Yjc2LWFmMGMtZjViMTBkZmRhN2NlXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BNTg2MGU4NWUtZTQ0MS00OWFkLTg0YmMtNDFlMDk2ZjNiOWFlXkEyXkFqcGdeQXVyMjM0MjgxNzU@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BM2UwY2MzOTYtYWY3ZS00NzM4LWE1YzQtZWE1MjYxNTQxODU2XkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BMTJlMjcwMGYtOWJhNi00MzdmLWE4NGYtNzE0Y2U1MWIzZTZjXkEyXkFqcGdeQXVyODEwNTIzOTM@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BYjJkMDViZDItOGY5NS00ZjkxLTk0ZjMtYjc3MmY2YjQ0ZWNjXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BZjhiNGUyZTMtMzYyOC00OWZkLWJjMzQtMjBlYjA0MmJhN2FmXkEyXkFqcGdeQXVyMTMxNDkzNDA3._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BNDk5ZDM2MTYtYTVmZC00OGQ0LTg1M2EtNzA3ZDMxMjM0MTAxXkEyXkFqcGdeQXVyNjgzNDU2ODI@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BYjAxMTI1ZWEtMDEyOC00YWY3LWJiNTMtZWY1YWQxNTYxMzNjXkEyXkFqcGdeQXVyMTE3OTA2MTY1._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BN2FjOGE3YzQtZTgxMC00Mjk1LThlNGEtMTc5YTU5ZjFhYzFkXkEyXkFqcGdeQXVyNjg5MjU3NjE@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BZGM2MjFlZTAtYzEzMy00ZTc3LTliNzAtMWQ1ODBiYTVlZTZhXkEyXkFqcGdeQXVyMzQ0MTAyNjY@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BYmY2MjE5YmYtMmEzZi00OTk5LWE2ODktMTEyNGZmN2M2ZTY5XkEyXkFqcGdeQXVyNjgzNDU2ODI@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BMDE3M2JmNGYtZDhjMC00YzZmLWI0MWUtYjVhZGYzMjEyZjFhXkEyXkFqcGdeQXVyMTEzOTc2MzQ3._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BYmY1MjU3N2MtMTA2Ny00YzlmLWIwY2EtM2I2MGQyMzY3YTA3XkEyXkFqcGdeQXVyMTEzOTc2MzQ3._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BYjQxNDNhMzQtZTE0MS00MDM3LWE2MDgtZDc2NTM1NjYzZjc3XkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BMTc1NjdmMDMtNWRkNy00MWMzLTg5MTMtZmM1ODIwMWIxZWMwXkEyXkFqcGdeQXVyMTE5MjIzMjMx._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BNzYwOTFhZjEtYTgwNC00YmRiLWEyNWUtZDgwMDc0NjZmNjUzXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BZTA1ZjFkODItNWNiNC00ODUxLTg4ZWItMWYzMjdlNDVkMTg0XkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BYjQ2MjQ4OGYtYzU5NC00YmEzLWJiMzUtZTNkNjA3MWNmYTM4XkEyXkFqcGdeQXVyODI4ODg2NzY@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BYTE5OTBlZTItODZmYi00YzA1LTgyMTktYTI0MTQ1ZGM4ZjQ1XkEyXkFqcGdeQXVyMTMyNDM5NTI0._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BMDMyZTQ4OGItZTRkNC00NjYyLWJkMjQtYTY1ZmFmZGIyMTAwXkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BZDI1ZGRkMWEtY2VmMC00MjVhLTg1NTQtNTcwMTcwNDcwZDJlXkEyXkFqcGdeQXVyMTMxNzQ5ODA1._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BNGViYzY5ZTMtMTFhOS00YjRjLTkxOWUtYjUxMWI0NzJiMDE4XkEyXkFqcGdeQXVyODMyNTM0MjM@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BMTcyMDZlMjItOGU3MC00MDJiLTgwZTctNjhhNGI3OWQwZGM3XkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BYzk4ZGJmZDYtMmUxNy00NzNiLWFhZWMtNWM3NDI1ZmE4ZTk1XkEyXkFqcGdeQXVyODkxNzAwMDI@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BOWJjY2ZlMzEtODA4YS00ODdkLTgyYmItYTQ2NTEyMmYzYzRhXkEyXkFqcGdeQXVyODExMDc5OTA@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BNDYyOWMzNTEtMjk0ZS00MDIxLTk2NWQtMmU4NDg4MTdkZWI3XkEyXkFqcGdeQXVyMTA4MjY1NjMz._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BOTQ4NDAwZGUtYzQ3ZC00YTAyLWIyNjItNmE0ZmNjNzdlMjJmXkEyXkFqcGdeQXVyODkxNzAwMDI@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BYTk2NmM0MDItYWMwMC00NjA0LTgyYWUtMDY3MGY3Zjc4MGFiXkEyXkFqcGdeQXVyODMyNTM0MjM@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BZDg5YjJjNjUtMjdiMS00MDgzLTgyNWQtMGI2NDUwN2NjMGJmXkEyXkFqcGdeQXVyMTAyMTU2OTc2._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BYmI2M2VlMGYtNTM5YS00YzIzLTlkOGEtNGFjOTJmMzRhZjc2XkEyXkFqcGdeQXVyODkxNzAwMDI@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BODk0NWE0MTAtOWU0NS00NzlkLWEzM2QtNDE0YWExY2JkMmYxXkEyXkFqcGdeQXVyODkxNzAwMDI@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BMjY4YTQxYWMtMDRjYy00OTAxLTk2ZTktZGU3ZDgyZWJkYjFiXkEyXkFqcGdeQXVyODkxNzAwMDI@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BZGRlZTNiYmEtMzdmZi00NjQxLWJkZjAtM2E0MzMzMGU3Y2ExXkEyXkFqcGdeQXVyODkxNzAwMDI@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BZmMxMmExYjktYzQ2My00NWI1LTkyZDgtYjkwYTdhMzkxYWE1XkEyXkFqcGdeQXVyODkxNzAwMDI@._V1_FMjpg_UX1000_.jpg",
        "https://m.media-amazon.com/images/M/MV5BMjYzY2Y1NTUtMzA1NS00OTYwLWIzYWUtMzk1ZmU2YTAwY2Q1XkEyXkFqcGdeQXVyODkxNzAwMDI@._V1_FMjpg_UX1000_.jpg"
    )
}
