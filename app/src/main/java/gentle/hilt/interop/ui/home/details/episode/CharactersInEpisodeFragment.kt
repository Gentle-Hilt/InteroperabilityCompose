package gentle.hilt.interop.ui.home.details.episode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import gentle.hilt.interop.databinding.FragmentCharactersInEpisodeBinding

@AndroidEntryPoint
class CharactersInEpisodeFragment : Fragment() {
    lateinit var binding: FragmentCharactersInEpisodeBinding
    private val viewModel: CharactersInEpisodeViewModel by viewModels()

    private val args: CharactersInEpisodeFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharactersInEpisodeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchEpisodeDetails(args.episode)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.episodeNumberTest.text = args.episode.toString()

        viewModel.observeCharacters(binding.charactersInEpisode)
        viewModel.observeEpisodeInfo(binding.episodeInfo)
    }
}
