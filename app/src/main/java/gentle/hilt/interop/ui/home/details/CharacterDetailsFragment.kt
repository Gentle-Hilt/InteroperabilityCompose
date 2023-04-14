package gentle.hilt.interop.ui.home.details

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import gentle.hilt.interop.databinding.FragmentCharacterDetailsBinding
import gentle.hilt.interop.network.models.CharacterDetails
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.fade_white
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.gray
import gentle.hilt.interop.ui.home.CharactersGridRecyclerView.Companion.white
import java.util.Locale

@AndroidEntryPoint
class CharacterDetailsFragment : Fragment() {
    lateinit var binding: FragmentCharacterDetailsBinding
    private val viewModel: CharacterDetailsViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.characterInEpisodes.episodes = character.episode
        detailsInLandScapeMode()
        binding.chImage.setContent {
            CharacterImage()
        }

        binding.apply {
            chName.setContent {
                BoxWithRowAndFadeTittle("Name"){
                    Text(character.name, color = Color(white), fontSize = 20.sp)
                }
            }

            chStatusGender.setContent {
                BoxWithRowAndFadeTittle("Status"){
                    StatusDot()
                    Spacer(Modifier.width(4.dp))
                    Text(character.status, color = Color.White, fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Gender", color = Color(fade_white), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    ImageGender()
                }
            }

            chLocation.setContent {
                BoxWithRowAndFadeTittle("Location"){
                    Text(color = Color(white), text = character.location.name.capitalize(), fontSize = 18.sp)
                }
            }

            chOrigin.setContent {
                BoxWithRowAndFadeTittle("Origin") {
                    Text(color = Color(white), text = character.origin.name.capitalize(), fontSize = 18.sp)
                }
            }
        }
    }

    private fun String.capitalize(): String {
        return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }

    private val args: CharacterDetailsFragmentArgs by navArgs()
    private lateinit var character: CharacterDetails
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        character = args.character
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun detailsInLandScapeMode(){
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val constrainLayout: ConstraintLayout = binding.clDetails
            val set = ConstraintSet()
            set.clone(constrainLayout)

            set.clear(R.id.chImage, ConstraintSet.END)
            set.clear(R.id.chImage, ConstraintSet.START)
            set.connect(R.id.chImage, ConstraintSet.START, R.id.clChTextInfo, ConstraintSet.END)
            set.connect(R.id.clChTextInfo, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

            set.applyTo(constrainLayout)
        }


    }

    @Composable
    fun CharacterImage() {
        val orientation = LocalContext.current.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT){
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(start= 55.dp, end = 55.dp, bottom = 90.dp, top = 20.dp)
                    .border(3.dp, Color.Gray, RoundedCornerShape(10.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(character.image),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp))

                )
            }

        }else{
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .padding(start = 20.dp, bottom = 20.dp, top = 10.dp)

            ) {
                Image(
                    painter = rememberAsyncImagePainter(character.image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp))

                )
            }
        }

    }

    @Composable
    fun ImageGender(){
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
    fun StatusDot(){
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

    @Composable
    fun BoxWithRowAndFadeTittle(chTittleInfo: String, content: @Composable () -> Unit) {
        val orientation = LocalContext.current.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Box(
                Modifier
                    .background(Color(gray), RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text("$chTittleInfo ", color = Color(fade_white), fontSize = 18.sp, textAlign = TextAlign.Start)

                    content()
                }

            }
        } else {
            Box(
                Modifier
                    .background(Color(gray), RoundedCornerShape(8.dp))
                    .wrapContentSize()
                    .padding(bottom = 5.dp,start = 5.dp,top = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text("$chTittleInfo ", color = Color(fade_white), fontSize = 18.sp)

                    content()
                }

            }
        }



    }
}
