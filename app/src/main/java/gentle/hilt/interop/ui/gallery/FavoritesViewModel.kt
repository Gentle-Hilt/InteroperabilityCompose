package gentle.hilt.interop.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import gentle.hilt.interop.data.room.repository.CharacterDetailsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val characterRepository: CharacterDetailsRepository
) : ViewModel() {
    fun observeFavorites(favoritesView: ListOfFavoriteCharactersView, navController: NavController) {
        viewModelScope.launch {
            characterRepository.observeCharacters().collect { favorites ->
                favoritesView.favorite = favorites
                favoritesView.navController = navController
            }
        }
    }
}
