package gentle.hilt.interop.ui.episode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import gentle.hilt.interop.databinding.FragmentCharactersInEpisodeBinding
import gentle.hilt.interop.network.NetworkStatus
import gentle.hilt.interop.ui.home.ErrorMessage

@AndroidEntryPoint
class CharactersInEpisodeFragment : Fragment() {
    private lateinit var binding: FragmentCharactersInEpisodeBinding
    private val viewModel: CharactersInEpisodeViewModel by viewModels()

    private val args: CharactersInEpisodeFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkInternet()
        viewModel.observeCharacters(binding.charactersInEpisode)
        viewModel.observeEpisodeInfo(binding.episodeInfo)
        reconnect()
    }

    private fun checkInternet() {
        if (!viewModel.connected()) {
            binding.noNetwork.visibility = View.VISIBLE
            binding.noNetwork.setContent { ErrorMessage() }
        }
    }

    private fun reconnect() {
        viewModel.networkObserve.observe(viewLifecycleOwner) { networkState ->
            when (networkState) {
                NetworkStatus.Available -> {
                    viewModel.fetchEpisodeDetails(args.episode)
                    binding.noNetwork.visibility = View.GONE
                    binding.linearLayout.visibility = View.VISIBLE
                }

                NetworkStatus.Unavailable -> {
                    viewModel.fetchEpisodeDetails(args.episode)
                    binding.noNetwork.visibility = View.GONE
                    binding.linearLayout.visibility = View.VISIBLE
                }
            }
        }
    }

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
}
