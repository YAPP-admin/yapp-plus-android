package com.yapp.plus.notification

import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.domain.notification.NotificationDeviceRepository
import com.yapp.plus.domain.notification.NotificationPreferenceRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class PushRegistration(
    private val environment: PushEnvironment,
    private val tokenStore: TokenStore,
    private val repository: NotificationDeviceRepository,
    private val preferences: NotificationPreferenceRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) {
    private val mutex = Mutex()
    private var registered: PushDeviceRegistration? = null

    fun isConfigured(): Boolean = environment.isConfigured()

    fun hasPermission(): Boolean = environment.hasPermission()

    suspend fun fetchEnabled(): Boolean = preferences.fetchEnabled()

    fun synchronize() {
        if (!isConfigured()) return
        scope.launch {
            try {
                synchronizeNow()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // Retry on resume, login, or token refresh.
            }
        }
    }

    internal suspend fun synchronizeNow() {
        mutex.withLock {
            if (!isConfigured()) return
            val sessionKey = tokenStore.currentTokens()?.storageKey
            if (sessionKey == null) {
                registered = null
                return
            }
            applyRegistration(
                enabled = preferences.fetchEnabled() && hasPermission(),
                sessionKey = sessionKey,
            )
        }
    }

    suspend fun updateEnabled(enabled: Boolean) {
        mutex.withLock {
            check(isConfigured())
            check(!enabled || hasPermission())
            val sessionKey = checkNotNull(tokenStore.currentTokens()?.storageKey)
            try {
                applyRegistration(enabled, sessionKey)
                requireCurrentSession(sessionKey)
                withContext(NonCancellable) { preferences.updateEnabled(enabled) }
            } catch (exception: Exception) {
                registered = null
                // Reconcile with the last persisted choice if the request or local save failed.
                synchronize()
                throw exception
            }
        }
    }

    private suspend fun applyRegistration(
        enabled: Boolean,
        sessionKey: String,
    ) {
        val installationId = environment.fetchInstallationId()
        requireCurrentSession(sessionKey)
        if (!enabled) {
            // After process death the server may still have a registration.
            repository.unregister(installationId)
            requireCurrentSession(sessionKey)
            registered = null
            return
        }
        val token = environment.fetchToken()
        requireCurrentSession(sessionKey)
        check(hasPermission())
        val registration = PushDeviceRegistration(sessionKey, installationId, token)
        if (registered == registration) return
        repository.register(installationId, token)
        requireCurrentSession(sessionKey)
        registered = registration
    }

    private suspend fun requireCurrentSession(sessionKey: String) {
        check(tokenStore.currentTokens()?.storageKey == sessionKey)
    }
}
