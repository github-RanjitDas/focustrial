package com.lawmobile.domain.repository.user

import com.lawmobile.domain.entities.User
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface UserRepository : BaseRepository {
    suspend fun getUserFromNetwork(uuid: String): Result<User>
    suspend fun getUserFromCamera(): Result<User>
}
