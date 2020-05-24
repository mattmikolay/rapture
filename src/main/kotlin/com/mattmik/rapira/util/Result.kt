package com.mattmik.rapira.util

sealed class Result<out T> {
    class Success<out T>(val obj: T) : Result<T>()
    class Error(val reason: String) : Result<Nothing>()

    companion object {
        fun <T, U> zip(result1: Result<T>, result2: Result<U>): Result<Pair<T, U>> =
            when (result1) {
                is Success -> when (result2) {
                    is Success -> Pair(result1.obj, result2.obj).toSuccess()
                    is Error -> result2
                }
                is Error -> result1
            }
    }
}

fun <T, U> Result<T>.map(transform: (T) -> U): Result<U> =
    when (this) {
        is Result.Success -> Result.Success(transform(this.obj))
        is Result.Error -> this
    }

fun <T> Result<T>.mapError(transform: (String) -> String): Result<T> =
    when (this) {
        is Result.Success -> this
        is Result.Error -> Result.Error(transform(this.reason))
    }

fun <T, U> Result<T>.andThen(transform: (T) -> Result<U>): Result<U> =
    when (this) {
        is Result.Success -> transform(this.obj)
        is Result.Error -> this
    }

fun <T> Result<T>.getOrThrow(buildException: (String) -> Exception): T =
    when (this) {
        is Result.Success -> this.obj
        is Result.Error -> throw buildException(this.reason)
    }

fun <T> T.toSuccess(): Result.Success<T> =
    Result.Success(this)
