package gentle.hilt.interop.network.cache

import gentle.hilt.interop.network.models.CharacterDetails
import gentle.hilt.interop.network.models.EpisodeDetails

object Cache{
    val character = mutableMapOf<Int, CharacterDetails>()
    val episode = mutableMapOf<Int, EpisodeDetails>()
    val charactersInEpisode = mutableMapOf<List<String>, List<CharacterDetails>>()
}
