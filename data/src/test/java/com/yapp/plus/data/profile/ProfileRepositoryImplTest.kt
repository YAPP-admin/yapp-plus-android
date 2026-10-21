package com.yapp.plus.data.profile

import com.yapp.plus.domain.profile.Profile
import com.yapp.plus.domain.signup.Name
import com.yapp.plus.domain.signup.PhoneNumber
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileRepositoryImplTest {
    @Test
    fun mapsFetchedDataIntoDomainValueObjects() =
        runTest {
            val repository = ProfileRepositoryImpl(FakeProfileDataSource())

            assertEquals(
                Profile(Name("홍길동"), PhoneNumber("01012345678")),
                repository.fetchProfile(),
            )
        }

    @Test
    fun mapsDomainProfileIntoDataSourceValues() =
        runTest {
            val dataSource = FakeProfileDataSource()
            val repository = ProfileRepositoryImpl(dataSource)

            repository.saveProfile(Profile(Name("홍길동"), PhoneNumber("01012345678")))

            assertEquals(ProfileData("홍길동", "01012345678"), dataSource.savedProfile)
        }
}

private class FakeProfileDataSource : ProfileDataSource {
    var savedProfile: ProfileData? = null

    override suspend fun fetchProfile(): ProfileData? =
        savedProfile ?: ProfileData("홍길동", "01012345678")

    override suspend fun saveProfile(profile: ProfileData) {
        savedProfile = profile
    }
}
