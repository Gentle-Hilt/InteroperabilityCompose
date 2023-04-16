package gentle.hilt.interop.ui.home.details.episode

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import coil.compose.rememberAsyncImagePainter
import gentle.hilt.interop.R
import gentle.hilt.interop.network.models.CharacterDetails
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.gray
import gentle.hilt.interop.ui.home.robotoFontFamily

@Composable
fun CharacterInEpisode(character: CharacterDetails, navController: NavController) {


    val action = CharactersInEpisodeFragmentDirections.actionCharactersInEpisodeFragmentToCharacterDetailsFragment(character)
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.DarkGray,
        modifier = Modifier
            .height(200.dp)
            .width(150.dp)
            .padding(8.dp)
            .clickable { navController.navigate(action) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10))
        ) {
            Image(
                painter = rememberAsyncImagePainter(character.image),
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(10))
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(Color(gray), RoundedCornerShape(10))
            ) {
                Text(
                    text = character.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = robotoFontFamily,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(3.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharactersInEpisodeList(characters: List<CharacterDetails>, navController: NavController) {
    val orientation = LocalContext.current.resources.configuration.orientation
    val numColumns = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        2
    } else {
        3
    }
    Box(
        modifier = Modifier
            .border(3.dp, Color.White, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10))
            .padding(bottom = 40.dp)

    ) {
        LazyVerticalGrid(
            state = rememberLazyGridState(),
            columns = GridCells.Fixed(numColumns),
            flingBehavior = rememberSnapFlingBehavior(rememberLazyListState())
        ) {
            items(characters.size) { index ->
                CharacterInEpisode(characters[index], navController)
            }
        }
    }
}
class CharactersInEpisodeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {
    // You don't wanna render problems, do you?
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setViewTreeSavedStateRegistryOwner(findViewTreeSavedStateRegistryOwner())
    }

    var characters: List<CharacterDetails>
        get() = charactersState
        set(value) {
            charactersState.clear()
            charactersState.addAll(value)
        }
    private val charactersState = mutableStateListOf<CharacterDetails>()

    @Composable
    override fun Content() {
        CharactersInEpisodeList(charactersState, findNavController())
    }
}
