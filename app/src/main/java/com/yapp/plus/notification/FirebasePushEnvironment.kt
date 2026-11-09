package com.yapp.plus.notification

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.yapp.plus.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.yapp.plus.core.network.BuildConfig as NetworkBuildConfig

internal class FirebasePushEnvironment
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) : PushEnvironment {
        override fun isConfigured(): Boolean {
            if (NetworkBuildConfig.MOCK_API_ENABLED) return false
            val expectedProject = if (BuildConfig.DEBUG) "yappplus-dev" else "yappplus-prod"
            return FirebaseApp.getApps(context).any {
                it.name == FirebaseApp.DEFAULT_APP_NAME && it.options.projectId == expectedProject
            }
        }

        override fun hasPermission(): Boolean = canShowPushNotifications(context)

        override suspend fun fetchInstallationId(): String = FirebaseInstallations.getInstance().id.await()

        @Suppress("DEPRECATION")
        override suspend fun fetchToken(): String = FirebaseMessaging.getInstance().token.await()
    }
