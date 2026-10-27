package com.yapp.plus.domain.auth

sealed interface AuthOperationResult {
    data object Success : AuthOperationResult

    data object Failure : AuthOperationResult

    data object NotSupported : AuthOperationResult
}
