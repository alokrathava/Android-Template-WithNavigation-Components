package com.theworld.androidtemplatewithnavcomponents.utils


sealed class Resource<out T> {

    data class Success<out T>(val value: T) : Resource<T>()

    object Loading : Resource<Nothing>()


    data class Failure(
        val errorCode: Int?,
        val isNetworkError: Boolean?,
        val errorBody: String?,
    ) : Resource<Nothing>()


}

