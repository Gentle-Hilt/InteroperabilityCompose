package gentle.hilt.interop.network.cache

import gentle.hilt.interop.network.models.CharacterDetailsModel
import gentle.hilt.interop.network.models.CharactersPage
import gentle.hilt.interop.network.models.EpisodeDetailsModel

object Cache {
    val character = mutableMapOf<Int, CharacterDetailsModel>()
    val episode = mutableMapOf<Int, EpisodeDetailsModel>()
    val charactersInEpisode = mutableMapOf<List<String>, List<CharacterDetailsModel>>()
    val searchPage = mutableMapOf<String, CharactersPage>()
}
