package gentle.hilt.interop.network

import gentle.hilt.interop.network.models.CharacterDetails
import gentle.hilt.interop.network.models.EpisodeDetails
import gentle.hilt.interop.network.models.GetCharactersPage
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("character")
    suspend fun fetchCharactersPage(
        @Query("page") pageIndex: Int
    ): Response<GetCharactersPage>

    @GET("episode/{episode-id}")
    suspend fun fetchEpisode(
        @Path("episode-id") episodeId: Int
    ): Response<EpisodeDetails>

    @GET("character/{list}")
    suspend fun fetchMultipleCharacters(
        @Path("list") charactersList: List<String>
    ): Response<List<CharacterDetails>>
}
