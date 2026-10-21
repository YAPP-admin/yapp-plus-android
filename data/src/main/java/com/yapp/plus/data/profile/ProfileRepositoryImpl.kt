package com.yapp.plus.data.profile

import com.yapp.plus.domain.profile.Profile
import com.yapp.plus.domain.profile.ProfileRepository
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber

class ProfileRepositoryImpl(
    private val dataSource: ProfileDataSource,
) : ProfileRepository {
    override suspend fun fetchProfile(): Profile? = dataSource.fetchProfile()?.toDomain()

    override suspend fun saveProfile(profile: Profile) {
        dataSource.saveProfile(profile.toData())
    }

    private fun ProfileData.toDomain() =
        Profile(
            name = Name(displayName),
            phoneNumber = PhoneNumber(phoneNumber),
        )

    private fun Profile.toData() =
        ProfileData(
            displayName = name.value,
            phoneNumber = phoneNumber.value,
        )
}
