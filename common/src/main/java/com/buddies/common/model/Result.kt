package com.buddies.common.model

sealed class Result<T> {
    data class Success<T>(val data: T? = null) : Result<T>()
    data class Fail<T>(val error: DefaultError) : Result<T>()
}