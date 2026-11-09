package com.yapp.plus.notification

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.yapp.plus.BuildConfig
import com.yapp.plus.core.network.auth.TokenStore
import com.yapp.plus.domain.notification.NotificationDeviceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.yapp.plus.core.network.BuildConfig as NetworkBuildConfig

@Singleton
internal class PushRegistration
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
        private val tokenStore: TokenStore,
        private val repository: NotificationDeviceRepository,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private val mutex = Mutex()
        private var registered: PushDeviceRegistration? = null

        fun isConfigured(): Boolean {
            if (NetworkBuildConfig.MOCK_API_ENABLED) return false
            val expectedProject = if (BuildConfig.DEBUG) "yappplus-dev" else "yappplus-prod"
            return FirebaseApp.getApps(context).any {
                it.name == FirebaseApp.DEFAULT_APP_NAME && it.options.projectId == expectedProject
            }
        }

        fun synchronize() {
            if (!isConfigured()) return
            scope.launch {
                mutex.withLock {
                    try {
                        synchronizeDevice()
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        // Retry when the app resumes or Firebase refreshes the token.
                    }
                }
            }
        }

        private suspend fun synchronizeDevice() {
            val sessionKey = tokenStore.currentTokens()?.storageKey
            if (sessionKey == null) {
                registered = null
                return
            }
            if (!canShowPushNotifications(context)) {
                unregisterDevice(sessionKey)
                return
            }

            val installationId = FirebaseInstallations.getInstance().id.await()

            // The YAPP sender targets FCM registration tokens.
            @Suppress("DEPRECATION")
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            if (tokenStore.currentTokens()?.storageKey != sessionKey) return
            if (!canShowPushNotifications(context)) return
            val registration = PushDeviceRegistration(sessionKey, installationId, fcmToken)
            if (registered == registration) return
            repository.register(installationId, fcmToken)
            registered = registration
        }

        private suspend fun unregisterDevice(sessionKey: String) {
            val registration = registered ?: return
            if (registration.sessionKey == sessionKey) {
                repository.unregister(registration.installationId)
            }
            registered = null
        }
    }
