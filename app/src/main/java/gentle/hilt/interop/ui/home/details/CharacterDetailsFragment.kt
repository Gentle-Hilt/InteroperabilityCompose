package gentle.hilt.interop.ui.home.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import gentle.hilt.interop.databinding.FragmentCharacterDetailsBinding
import gentle.hilt.interop.network.models.CharacterDetails

@AndroidEntryPoint
class CharacterDetailsFragment : Fragment() {
    lateinit var binding: FragmentCharacterDetailsBinding
    private val viewModel: CharacterDetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.testCharacterDetails.text = character.name

        binding.characterInEpisodes.episodes = character.episode
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
}
