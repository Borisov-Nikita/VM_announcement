package nik.borisov.vmannouncement.utils

import retrofit2.Response

abstract class NetworkResponse {

    suspend fun <T> safeNetworkCall(call: suspend () -> Response<T>): DataResult<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return DataResult.Success(body)
                } ?: return getErrorResult("Response body is empty")
            } else {
                return getErrorResult("${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            return getErrorResult(e.message.toString())
        }
    }

    private fun <T> getErrorResult(error: String): DataResult.Error<T> {
        return DataResult.Error(message = "Network call failed: $error")
    }
}