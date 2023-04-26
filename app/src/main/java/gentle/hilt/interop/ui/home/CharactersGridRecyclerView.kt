package gentle.hilt.interop.ui.home

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import coil.compose.rememberAsyncImagePainter
import gentle.hilt.interop.R
import gentle.hilt.interop.network.models.CharacterDetailsModel
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.gray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Composable
fun Item(character: CharacterDetailsModel, navController: NavController) {
    val action = HomeFragmentDirections.actionNavHomeToCharacterDetailsFragment(character)
    // Managing your item functionality and look at one place
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .height(200.dp)
            .width(150.dp)
            .padding(5.dp)
            .clickable { navController.navigate(action) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(3.dp, Color.Gray, RoundedCornerShape(10.dp))
        ) {
            Image(
                painter = rememberAsyncImagePainter(character.image),
                contentDescription = null,
                modifier = Modifier
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
fun Grid(characters: Flow<PagingData<CharacterDetailsModel>>, navController: NavController) {
    // read collectAsLazyPagingItems description
    val lazyPaging = characters.collectAsLazyPagingItems()

    val orientation = LocalConfiguration.current.orientation
    val numColumns = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        2
    } else {
        3
    }
    // If you think to add header to LazyVerticalGrid https://issuetracker.google.com/issues/231557184
    // this will help after fix
    /*groupedData.forEach { (group, items) ->
        stickyHeader {}
        val groupedData = lazyPaging.itemSnapshotList.items
            .groupBy { it.name.firstOrNull()?.uppercase() ?: '#' }
            .toMap()*/
    LazyVerticalGrid(
        state = rememberLazyGridState(),
        columns = GridCells.Fixed(numColumns),
        flingBehavior = rememberSnapFlingBehavior(rememberLazyListState()),
        content = {
            items(lazyPaging.itemCount) { index ->
                lazyPaging[index]?.let { character ->
                    Item(character, navController)
                }
            }
            when (lazyPaging.loadState.append) {
                is LoadState.Error -> {
                    val message = (lazyPaging.loadState.append as? LoadState.Error)?.error?.message
                        ?: return@LazyVerticalGrid

                    item(span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                        ErrorScreen(
                            message = message,
                            modifier = Modifier.padding(vertical = 8.dp),
                            refresh = { lazyPaging.retry() }
                        )
                    }
                }

                else -> Unit
            }
        }
    )
}

@Composable
fun ErrorScreen(message: String, modifier: Modifier = Modifier, refresh: () -> Unit) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = refresh, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)) {
            Text(text = stringResource(id = R.string.retry), color = Color.White)
        }
    }
}

class CharactersGridRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    // You don't wanna render problems, do you?
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setViewTreeSavedStateRegistryOwner(findViewTreeSavedStateRegistryOwner())
    }

    private val emptyPagedData = PagingData.empty<CharacterDetailsModel>()
    private val emptyFlowPagedData: Flow<PagingData<CharacterDetailsModel>> = flow {
        emit(emptyPagedData)
    }

    // state that will allow UI to update
    private val uiState = mutableStateOf(emptyFlowPagedData)

    // pagedData to get and set to update the UI
    var pagedData: Flow<PagingData<CharacterDetailsModel>>
        get() = uiState.value
        set(value) {
            uiState.value = value
        }

    @Composable
    override fun Content() {
        //  Content now uses Grid composable to display the pagedData
        Grid(characters = uiState.value, findNavController())
    }
    companion object {
        const val gray: Long = 0xFF3c3e44
        const val fade_white: Long = 0xFF979792
        const val white: Long = 0xfff5f5f5
        const val light_dark: Long = 0xFF343541
        const val bright_blue: Long = 0xff007FFF
        const val red_bright: Long = 0xffF10449
    }
}
