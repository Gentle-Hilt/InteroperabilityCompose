package gentle.hilt.interop.network

import gentle.hilt.interop.network.models.CharacterDetails
import gentle.hilt.interop.network.models.CharactersPage
import gentle.hilt.interop.network.models.EpisodeDetails
import gentle.hilt.interop.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject

class ApiClient @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCharactersPage(pageIndex: Int): ResponseState<CharactersPage> {
        return safeApiCall { apiService.fetchCharactersPage(pageIndex) }
    }

    suspend fun getEpisode(episodeId: Int): ResponseState<EpisodeDetails> {
        return safeApiCall { apiService.fetchEpisode(episodeId) }
    }

    suspend fun getCharacters(characters: List<String>): ResponseState<List<CharacterDetails>> {
        return safeApiCall { apiService.fetchMultipleCharacters(characters) }
    }

    suspend fun searchCharacterPage(
        characterName: String,
        pageIndex: Int
    ): ResponseState<CharactersPage> {
        return safeApiCall { apiService.searchCharacterPage(characterName, pageIndex) }
    }

    private inline fun <T> safeApiCall(apiCall: () -> Response<T>): ResponseState<T> {
        return try {
            val response = apiCall.invoke()
            if (response.isSuccessful) {
                ResponseState.success(response)
            } else {
                ResponseState.failure(Exception("HTTP Error ${response.code()}"))
            }
        } catch (e: Exception) {
            ResponseState.failure(Exception("Network Error: ${e.message}"))
        }
    }
}
