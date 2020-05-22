package com.mattmik.rapira.objects

sealed class OperationResult {
    class Success(val obj: RObject) : OperationResult()
    class Error(val reason: String) : OperationResult()
}

fun OperationResult.map(transform: (RObject) -> RObject): OperationResult =
    when (this) {
        is OperationResult.Success -> OperationResult.Success(transform(this.obj))
        is OperationResult.Error -> this
    }

fun OperationResult.mapError(transform: (String) -> String): OperationResult =
    when (this) {
        is OperationResult.Success -> this
        is OperationResult.Error -> OperationResult.Error(transform(this.reason))
    }

fun OperationResult.andThen(transform: (RObject) -> OperationResult): OperationResult =
    when (this) {
        is OperationResult.Success -> transform(this.obj)
        is OperationResult.Error -> this
    }

fun OperationResult.getOrThrow(buildException: (String) -> Exception) =
    when (this) {
        is OperationResult.Success -> this.obj
        is OperationResult.Error -> throw buildException(this.reason)
    }
