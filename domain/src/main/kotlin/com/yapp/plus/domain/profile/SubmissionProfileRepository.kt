package com.yapp.plus.domain.profile

interface SubmissionProfileRepository {
    suspend fun fetchProfile(): SubmissionProfile?

    suspend fun saveProfile(profile: SubmissionProfile)
}
