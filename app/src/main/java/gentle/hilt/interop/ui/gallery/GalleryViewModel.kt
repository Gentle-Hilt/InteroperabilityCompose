package gentle.hilt.interop.ui.gallery

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gentle.hilt.interop.data.room.repository.CharacterDetailsRepository
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    characterRepository: CharacterDetailsRepository
) : ViewModel() {

    val observeFavorites = characterRepository.observeCharacters()
}
