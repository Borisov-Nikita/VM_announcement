package nik.borisov.vmannouncement.utils

sealed class DataResult<T>(
    val data: T? = null,
    val message: String? = null
) {

    class Success<T>(data: T?) : DataResult<T>(data = data)
    class Error<T>(message: String?) : DataResult<T>(message = message)
    class Loading<T>() : DataResult<T>()
}
