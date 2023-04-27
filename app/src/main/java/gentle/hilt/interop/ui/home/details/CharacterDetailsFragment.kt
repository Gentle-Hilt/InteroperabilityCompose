package gentle.hilt.interop.ui.home.details

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.compose.rememberAsyncImagePainter
import dagger.hilt.android.AndroidEntryPoint
import gentle.hilt.interop.R
import gentle.hilt.interop.data.room.entities.CharacterDetailsEntity
import gentle.hilt.interop.data.room.mappers.toEntity
import gentle.hilt.interop.databinding.FragmentCharacterDetailsBinding
import gentle.hilt.interop.network.NetworkStatus
import gentle.hilt.interop.network.models.CharacterDetailsModel
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.fade_white
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.gray
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.white
import gentle.hilt.interop.ui.home.robotoFontFamily
import java.util.Locale

@AndroidEntryPoint
class CharacterDetailsFragment : Fragment() {
    lateinit var binding: FragmentCharacterDetailsBinding
    private val viewModel: CharacterDetailsViewModel by viewModels()

    private val args: CharacterDetailsFragmentArgs by navArgs()
    private lateinit var character: CharacterDetailsModel
    private lateinit var mappedCharacter: CharacterDetailsEntity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        character = args.character
        mappedCharacter = character.toEntity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        characterDetailsInLandScapeMode()

        binding.apply {
            chImage.setContent { CharacterImage() }
            chName.setContent { BoxWithRowAndFadeTittle("Name:", character.name) {} }
            chStatusGender.setContent { StatusGender() }
            btnAdd.setContent { FavoriteCharacterButton() }
            chLocation.setContent { BoxWithRowAndFadeTittle("Location:", character.location.name.capitalize()) {} }
            chOrigin.setContent { BoxWithRowAndFadeTittle("Origin:", character.origin.name.capitalize()) {} }
            characterInEpisodes.episodes = character.episode
        }
        reconnect()
    }

    private fun reconnect() {
        viewModel.networkState.observe(viewLifecycleOwner) { networkState ->
            when (networkState) {
                NetworkStatus.Available -> {
                    binding.chImage.setContent { CharacterImage() }
                }

                NetworkStatus.Unavailable -> Unit
            }
        }
    }

    @Composable
    fun StatusGender() {
        BoxWithRowAndFadeTittle("Status:", character.status.capitalize()) {
            Spacer(Modifier.width(4.dp))
            StatusDot()
            Spacer(Modifier.width(8.dp))
            Text(
                "Gender:",
                color = Color(fade_white),
                fontFamily = robotoFontFamily,
                fontStyle = FontStyle.Italic,
                fontSize = 20.sp
            )
            ImageGender()
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun FavoriteCharacterButton() {
        val isFavorite = viewModel.isCharacterFavorite(mappedCharacter).collectAsState(initial = false)
        val orientation = LocalConfiguration.current.orientation
        val portrait: Boolean = orientation == Configuration.ORIENTATION_PORTRAIT
        val paddingModifier = if (portrait) Modifier.padding(bottom = 45.dp, end = 50.dp) else Modifier
        IconButton(
            modifier = Modifier.then(paddingModifier),
            onClick = {
                if (isFavorite.value) {
                    viewModel.deleteCharacterFromFavorite(mappedCharacter)
                } else {
                    viewModel.addCharacterAsFavorite(mappedCharacter)
                }
            }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(60.dp)
            ) {
                PulsatingIcon(
                    icon = Icons.Default.Favorite,
                    color = if (isFavorite.value) Color.Red else Color.Black
                )
            }
        }
    }

    @Composable
    fun PulsatingIcon(
        icon: ImageVector,
        color: Color,
        pulseDuration: Int = 1000,
        pulseMagnitude: Float = 1.1f
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "")

        val scale by infiniteTransition.animateFloat(
            label = "",
            initialValue = 1f,
            targetValue = pulseMagnitude,
            animationSpec = infiniteRepeatable(
                animation = tween(pulseDuration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Icon(icon, null, tint = color, modifier = Modifier.scale(scale).size(70.dp))
    }

    @Composable
    fun CharacterImage() {
        val orientation = LocalConfiguration.current.orientation
        val portrait: Boolean = orientation == Configuration.ORIENTATION_PORTRAIT
        val sizeModifier = if (portrait) Modifier.aspectRatio(1f) else Modifier.size(180.dp)
        val paddingModifier = if (portrait) {
            Modifier.padding(40.dp)
        } else {
            Modifier.padding(
                start = 20.dp,
                bottom = 20.dp,
                top = 10.dp
            )
        }
        val contentScale = if (portrait) ContentScale.Fit else ContentScale.Crop
        Box(
            modifier = Modifier
                .then(sizeModifier)
                .then(paddingModifier)
                .border(3.dp, Color.Gray, RoundedCornerShape(10.dp))
        ) {
            Image(
                painter = rememberAsyncImagePainter(character.image),
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp))

            )
        }
    }

    @Composable
    fun BoxWithRowAndFadeTittle(tittle: String, info: String, content: @Composable () -> Unit) {
        val orientation = LocalConfiguration.current.orientation
        val portrait: Boolean = orientation == Configuration.ORIENTATION_PORTRAIT

        val sizeModifier = if (portrait) Modifier.fillMaxWidth() else Modifier.wrapContentSize()
        val paddingModifier = if (portrait) {
            Modifier.padding(8.dp)
        } else {
            Modifier.padding(
                bottom = 5.dp,
                start = 5.dp,
                top = 5.dp
            )
        }

        Box(
            Modifier
                .background(Color(gray), RoundedCornerShape(8.dp))
                .then(paddingModifier)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .then(sizeModifier)
            ) {
                Text(
                    "$tittle ",
                    color = Color(fade_white),
                    fontSize = 18.sp,
                    fontFamily = robotoFontFamily,
                    fontStyle = FontStyle.Normal
                )

                Text(
                    info,
                    color = Color(white),
                    fontSize = 18.sp,
                    fontFamily = robotoFontFamily,
                    fontStyle = FontStyle.Normal
                )

                content()
            }
        }
    }

    @Composable
    fun ImageGender() {
        Image(
            painter = painterResource(
                id = when (character.gender.lowercase()) {
                    "male" -> R.drawable.male_g
                    "female" -> R.drawable.female_g
                    else -> R.drawable.question_mark
                }
            ),
            contentDescription = "Gender Icon",
            modifier = Modifier.size(24.dp)
        )
    }

    @Composable
    fun StatusDot() {
        Box(
            Modifier.size(8.dp).background(
                when (character.status.lowercase()) {
                    "alive" -> Color.Green
                    "dead" -> Color.Red
                    else -> Color.Gray
                },
                CircleShape
            )
        )
    }

    private fun characterDetailsInLandScapeMode() {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val constrainLayout: ConstraintLayout = binding.clDetails
            val set = ConstraintSet()
            set.clone(constrainLayout)

            set.clear(R.id.chImage, ConstraintSet.END)
            set.clear(R.id.chImage, ConstraintSet.START)
            set.clear(R.id.chImage, ConstraintSet.BOTTOM)
            set.connect(R.id.chImage, ConstraintSet.END, R.id.clChTextInfo, ConstraintSet.START, 20)
            set.connect(R.id.clChTextInfo, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            set.connect(R.id.btnAdd, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            set.connect(R.id.btnAdd, ConstraintSet.TOP, R.id.appBarLayout, ConstraintSet.TOP, 20)
            set.connect(R.id.btnAdd, ConstraintSet.START, R.id.clChTextInfo, ConstraintSet.END)

            set.applyTo(constrainLayout)
        }
    }

    private fun String.capitalize(): String {
        return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterDetailsBinding.inflate(layoutInflater)
        return binding.root
    }
}
