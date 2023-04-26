package gentle.hilt.interop.network.service

import gentle.hilt.interop.network.models.CharacterDetailsModel
import gentle.hilt.interop.network.models.CharactersPage
import gentle.hilt.interop.network.models.EpisodeDetailsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("character")
    suspend fun fetchCharactersPage(
        @Query("page") pageIndex: Int
    ): Response<CharactersPage>

    @GET("episode/{episode-id}")
    suspend fun fetchEpisode(
        @Path("episode-id") episodeId: Int
    ): Response<EpisodeDetailsModel>

    @GET("character/{list}")
    suspend fun fetchMultipleCharacters(
        @Path("list") charactersList: List<String>
    ): Response<List<CharacterDetailsModel>>

    @GET("character")
    suspend fun searchCharacterPage(
        @Query("name") characterName: String,
        @Query("page") pageIndex: Int
    ): Response<CharactersPage>
}
