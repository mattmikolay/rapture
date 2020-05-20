package com.mattmik.rapira.objects

sealed class OperationResult {
    class Success(val obj: RObject) : OperationResult()
    class Error(val reason: String) : OperationResult()
}
