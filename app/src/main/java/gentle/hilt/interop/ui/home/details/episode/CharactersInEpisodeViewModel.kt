package gentle.hilt.interop.ui.home.details.episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gentle.hilt.interop.network.NetworkRepository
import gentle.hilt.interop.network.models.CharacterDetails
import gentle.hilt.interop.network.models.EpisodeDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharactersInEpisodeViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {
    val episodeState = MutableStateFlow(EpisodeDetails())
    private val charactersState = MutableStateFlow<List<CharacterDetails>>(emptyList())

    fun fetchEpisodeDetails(episodeId: Int) {
        viewModelScope.launch {
            val epResponse = repository.getEpisode(episodeId) ?: return@launch
            episodeState.value = epResponse

            val charactersInEpisode = epResponse.characters.map { link ->
                link.substringAfterLast("/")
            }

            val chResponse = repository.getCharactesrs(charactersInEpisode) ?: return@launch
            charactersState.value = chResponse
        }
    }

    fun observeCharacters(charactersInEpisodeView: CharactersInEpisodeView) {
        viewModelScope.launch {
            charactersState.collect { charactersInEpisode ->
                charactersInEpisodeView.characters = charactersInEpisode
            }
        }
    }

    fun observeEpisodeInfo(episodeInfoView: EpisodeInfoView) {
        viewModelScope.launch {
            episodeState.collect { episodeDetails ->
                episodeInfoView.episodeInfo = episodeDetails
            }
        }
    }
}
