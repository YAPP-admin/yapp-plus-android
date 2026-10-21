package com.yapp.plus.data.profile

import com.yapp.plus.data.supabase.SupabaseRestClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseProfileDataSource(
    private val client: SupabaseRestClient,
) : ProfileDataSource {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun fetchProfile(): ProfileData? =
        client.fetch(
            path = PROFILE_TABLE,
            parameters = listOf("select" to "display_name,phone_number", "limit" to "2"),
        ) { body ->
            val profiles = json.decodeFromString<List<SupabaseProfileResponse>>(body)
            require(profiles.size <= 1)
            profiles.firstOrNull()?.toData()
        }

    override suspend fun saveProfile(profile: ProfileData) {
        client.upsert(
            path = PROFILE_TABLE,
            conflictColumns = "user_id",
            body =
                buildJsonObject {
                    put("display_name", profile.displayName)
                    put("phone_number", profile.phoneNumber)
                },
        ) { Unit }
    }

    private fun SupabaseProfileResponse.toData(): ProfileData {
        require(displayName.isNotBlank())
        require(phoneNumber.isNotBlank())
        return ProfileData(displayName, phoneNumber)
    }

    @Serializable
    private data class SupabaseProfileResponse(
        @SerialName("display_name") val displayName: String,
        @SerialName("phone_number") val phoneNumber: String,
    )

    private companion object {
        const val PROFILE_TABLE = "mvp_my_profiles"
    }
}
