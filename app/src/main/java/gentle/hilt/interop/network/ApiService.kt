package gentle.hilt.interop.network



import gentle.hilt.interop.network.models.GetCharactersPage
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService{

    @GET("character")
    suspend fun fetchCharactersPage(
        @Query("page") pageIndex: Int
    ): Response<GetCharactersPage>



}
