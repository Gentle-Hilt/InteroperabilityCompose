package gentle.hilt.interop.network

import android.content.Context
import gentle.hilt.interop.R
import gentle.hilt.interop.network.cache.Cache
import gentle.hilt.interop.network.models.CharacterDetails
import gentle.hilt.interop.network.models.EpisodeDetails
import gentle.hilt.interop.network.models.GetCharactersPage
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val apiClient: ApiClient,
    context: Context
) {
    val exceptionNetworkMessage: String = "${context.getString(R.string.check_internet)} \n" + context.getString(R.string.failed_to_load)
    val isLoading = MutableStateFlow(false)
    suspend fun getCharactersPage(pageIndex: Int): GetCharactersPage? {
        isLoading.value = true
        val request = apiClient.getCharactersPage(pageIndex)
        isLoading.value = false

        if (!request.isSuccessful || request.failed) {
            return request.bodyNullable
        }

        return request.body
    }

    suspend fun getEpisode(episodeId: Int): EpisodeDetails? {
        isLoading.value = true
        val cachedEpisode = Cache.episode[episodeId]
        if(cachedEpisode != null){
            isLoading.value = false
            return cachedEpisode
        }
        val request = apiClient.getEpisode(episodeId)
        isLoading.value = false

        if (!request.isSuccessful || request.failed) {
            return request.bodyNullable
        }
        Cache.episode[episodeId] = request.body
        return request.body
    }

    suspend fun getCharacters(characters: List<String>): List<CharacterDetails>? {
        isLoading.value = true
        val cachedCharacters = Cache.charactersInEpisode[characters]
        if(cachedCharacters != null){
            isLoading.value = false
            return cachedCharacters
        }
        val request = apiClient.getCharacters(characters)
        isLoading.value = false

        if (!request.isSuccessful || request.failed) {
            return request.bodyNullable
        }

        Cache.charactersInEpisode[characters] = request.body
        return request.body
    }
}
