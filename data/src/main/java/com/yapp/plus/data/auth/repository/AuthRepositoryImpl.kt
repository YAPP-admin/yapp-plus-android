package com.yapp.plus.data.auth.repository

import com.yapp.plus.core.network.NetworkRequestResult
import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.core.network.auth.Tokens
import com.yapp.plus.core.network.invokeRequest
import com.yapp.plus.data.auth.datasource.auth.AuthDataSource
import com.yapp.plus.data.auth.datasource.auth.AuthResponseResult
import com.yapp.plus.domain.auth.AuthFailure
import com.yapp.plus.domain.auth.AuthRepository
import com.yapp.plus.domain.auth.AuthResult
import com.yapp.plus.domain.auth.GoogleLoginCredential
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber
import kotlinx.coroutines.CancellationException

class AuthRepositoryImpl(
    private val dataSource: AuthDataSource,
    private val tokenStore: TokenStore,
) : AuthRepository {
    override suspend fun authenticateWithGoogle(credential: GoogleLoginCredential): AuthResult =
        execute {
            dataSource.authenticateWithGoogle(
                idToken = credential.idToken,
                nonce = credential.rawNonce,
            )
        }

    override suspend fun register(
        registrationToken: String,
        name: Name,
        phoneNumber: PhoneNumber,
    ): AuthResult =
        execute {
            val e164PhoneNumber =
                phoneNumber.toE164OrNull()
                    ?: return@execute AuthResponseResult.Failed(AuthFailure.InvalidRequest)
            dataSource.register(
                registrationToken = registrationToken,
                name = name.value.trim(),
                phoneNumber = e164PhoneNumber,
            )
        }

    override suspend fun hasStoredSession(): Boolean = tokenStore.currentTokens() != null

    private suspend fun execute(request: suspend () -> AuthResponseResult): AuthResult {
        val responseResult =
            when (val requestResult = invokeRequest(request)) {
                is NetworkRequestResult.Success -> requestResult.data
                is NetworkRequestResult.Error -> return AuthResult.Failed(AuthFailure.Unavailable)
            }

        return try {
            when (val result = responseResult) {
                is AuthResponseResult.SignedIn -> {
                    tokenStore.updateTokens(Tokens(result.accessToken, result.refreshToken))
                    AuthResult.SignedIn
                }

                is AuthResponseResult.SignupRequired ->
                    AuthResult.SignupRequired(result.registrationToken)

                is AuthResponseResult.Failed -> AuthResult.Failed(result.reason, result.fieldErrors)
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            AuthResult.Failed(AuthFailure.Unavailable)
        }
    }
}
