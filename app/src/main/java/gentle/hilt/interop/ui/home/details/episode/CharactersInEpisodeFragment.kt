package gentle.hilt.interop.ui.home.details.episode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
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
        loading(binding.pbLoading)
        reconnect()
    }

    private fun checkInternet() {
        if (!viewModel.connected()) {
            binding.noNetwork.visibility = View.VISIBLE
            binding.noNetwork.setContent { ErrorMessage() }
        }
    }
    private fun loading(loading: ComposeView) {
        loading.setContent {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = Color.Green,
                strokeWidth = 7.dp

            )
        }
        viewModel.loadingState(loading)
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
