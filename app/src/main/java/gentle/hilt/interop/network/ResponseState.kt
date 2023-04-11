package gentle.hilt.interop.network

import retrofit2.Response

data class ResponseState<T>(
    val status: Status,
    val data: Response<T>?,
    val exception: Exception?,
) {
    sealed class Status {
        object Success : Status()
        object Failure : Status()
    }

    companion object {
        fun <T> success(data: Response<T>) = ResponseState(Status.Success, data, null)
        fun <T> failure(exception: Exception) = ResponseState<T>(Status.Failure, null, exception)
    }

    val failed: Boolean
        get() = this.status == Status.Failure

    val isSuccessful: Boolean
        get() = !failed && this.data?.isSuccessful == true

    // In fact i need only this at runtime
    val body: T
        get() = this.data!!.body()!!

    val bodyNullable: T?
        get() = this.data?.body()

}