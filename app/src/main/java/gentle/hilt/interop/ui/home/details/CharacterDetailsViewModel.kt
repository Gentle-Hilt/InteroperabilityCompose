package gentle.hilt.interop.ui.home.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gentle.hilt.interop.data.room.entities.CharacterDetailsEntity
import gentle.hilt.interop.data.room.repository.CharacterDetailsRepository
import gentle.hilt.interop.network.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val networkRepo: NetworkRepository,
    val characterRepo: CharacterDetailsRepository
) : ViewModel() {
    val networkState = networkRepo.networkStatus.asLiveData(Dispatchers.IO)

    fun addCharacterAsFavorite(character: CharacterDetailsEntity) {
        viewModelScope.launch {
            characterRepo.insertCharacter(character)
        }
    }
    fun deleteCharacterFromFavorite(character: CharacterDetailsEntity) {
        viewModelScope.launch {
            characterRepo.deleteCharacter(character)
        }
    }

    fun observeFavorites(character: CharacterDetailsEntity) {
        viewModelScope.launch {
            characterRepo.observeCharacters().collect { listCharacters ->
                listCharacters.map { character ->
                }
            }
        }
    }
}
