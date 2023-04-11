package gentle.hilt.interop.network

import android.content.Context
import gentle.hilt.interop.R
import gentle.hilt.interop.network.models.GetCharactersPage
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val apiClient: ApiClient,
    context: Context
){
    val exceptionNetworkMessage: String = "${context.getString(R.string.check_internet)} \n" + context.getString(R.string.failed_to_load)
    val isLoading = MutableStateFlow(false)
    suspend fun getCharactersPage(pageIndex:Int): GetCharactersPage?{
        isLoading.value = true
        val request = apiClient.getCharactersPage(pageIndex)
        isLoading.value = false

        if(!request.isSuccessful || request.failed){
            return request.bodyNullable
        }

        return request.body
    }

}
