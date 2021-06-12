package com.theworld.androidtemplatewithnavcomponents.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

abstract class SafeApiCall {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {

        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (exception: Exception) {

                when (exception) {
                    is HttpException -> {
                        Resource.Failure(exception.code(), false, exception.message())
                    }
                    is IOException -> {
                        Resource.Failure(null, true, exception.message)
                    }
                    else -> Resource.Failure(null, true, exception.message)
                }

            }
        }
    }
}

