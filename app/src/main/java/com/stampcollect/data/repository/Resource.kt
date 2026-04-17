package com.stampcollect.data.repository

import com.stampcollect.data.exceptions.AppException

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Loading(val isRefresh: Boolean = false) : Resource<Nothing>()
    data class Error(val error: Throwable) : Resource<Nothing>()

    val isLoading: Boolean
        get() = this is Loading

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    fun data(): T? = (this as? Success)?.data

    fun error(): Throwable = (this as? Error)?.error ?: AppException()
}
