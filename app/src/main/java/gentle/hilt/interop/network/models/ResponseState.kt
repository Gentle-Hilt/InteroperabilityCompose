package gentle.hilt.interop.network.models

import retrofit2.Response

data class ResponseState<T>(
    val data: Response<T>?
)
