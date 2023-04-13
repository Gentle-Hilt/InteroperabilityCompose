package gentle.hilt.interop.network

import gentle.hilt.interop.network.models.CharacterDetails
import gentle.hilt.interop.network.models.EpisodeDetails
import gentle.hilt.interop.network.models.GetCharactersPage
import retrofit2.Response
import javax.inject.Inject

class ApiClient @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCharactersPage(pageIndex: Int): ResponseState<GetCharactersPage> {
        return safeApiCall { apiService.fetchCharactersPage(pageIndex) }
    }

    suspend fun getEpisode(episodeId: Int): ResponseState<EpisodeDetails> {
        return safeApiCall { apiService.fetchEpisode(episodeId) }
    }

    suspend fun getCharacters(characters: List<String>): ResponseState<List<CharacterDetails>> {
        return safeApiCall { apiService.fetchMultipleCharacters(characters) }
    }

    private inline fun <T> safeApiCall(apiCall: () -> Response<T>): ResponseState<T> {
        return try {
            ResponseState.success(apiCall.invoke())
        } catch (e: Exception) {
            ResponseState.failure(e)
        }
    }
}
