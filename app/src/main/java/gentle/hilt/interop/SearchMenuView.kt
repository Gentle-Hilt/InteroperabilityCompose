package gentle.hilt.interop

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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import gentle.hilt.interop.data.datastore.DataStoreManager
import gentle.hilt.interop.network.models.CharacterDetailsModel
import gentle.hilt.interop.theme.robotoFontFamily
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.gray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun Item(
    character: CharacterDetailsModel,
    navController: NavController?,
    closeSearchList: () -> Unit,
    dataStore: DataStoreManager?
) {
    val navOptions = NavOptions.Builder()
        .setEnterAnim(R.anim.slide_from_right)
        .setExitAnim(R.anim.slide_to_left)
        .setPopExitAnim(R.anim.slide_to_right)
        .setPopEnterAnim(R.anim.slide_from_left)
        .build()
    val coroutineScope = rememberCoroutineScope()
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .height(200.dp)
            .width(150.dp)
            .padding(5.dp)
            .clickable {
                navController?.navigate(
                    R.id.characterDetailsFragment,
                    bundleOf("character" to character),
                    navOptions
                )
                closeSearchList()
                coroutineScope.launch {
                    Timber.d("dataStore false")
                    dataStore?.saveSearchMenuState(false)
                    dataStore?.saveLastCharacterSearch(character.name)
                }
            }
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
fun Grid(
    characters: Flow<PagingData<CharacterDetailsModel>>,
    navController: NavController?,
    closeSearchList: () -> Unit,
    dataStore: DataStoreManager?
) {
    val lazyPaging = characters.collectAsLazyPagingItems()
    val orientation = LocalConfiguration.current.orientation
    val numColumns = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        2
    } else {
        3
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color(gray)),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            state = rememberLazyGridState(),
            columns = GridCells.Fixed(numColumns),
            flingBehavior = rememberSnapFlingBehavior(rememberLazyListState()),
            content = {
                items(lazyPaging.itemCount) { index ->
                    lazyPaging[index]?.let { character ->
                        Item(character, navController, closeSearchList, dataStore)
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

class SearchMenuView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    var navController: NavController? = null,
    var dataStore: DataStoreManager? = null
) : AbstractComposeView(context, attrs, defStyleAttr) {

    private val emptyPagedData = PagingData.empty<CharacterDetailsModel>()
    private val emptyFlowPagedData: Flow<PagingData<CharacterDetailsModel>> = flow {
        emit(emptyPagedData)
    }
    private val uiState = mutableStateOf(emptyFlowPagedData)
    var pagedData: Flow<PagingData<CharacterDetailsModel>>
        get() = uiState.value
        set(value) {
            uiState.value = value
        }

    @Composable
    override fun Content() {
        Grid(
            characters = uiState.value,
            navController = navController,
            closeSearchList = { visibility = GONE },
            dataStore = dataStore
        )
    }
}
