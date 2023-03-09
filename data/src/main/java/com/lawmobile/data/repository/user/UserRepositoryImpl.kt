package com.lawmobile.data.repository.user

import com.lawmobile.data.datasource.remote.user.UserRemoteDataSource
import com.lawmobile.data.mappers.impl.UserMapper.toDomain
import com.lawmobile.domain.entities.User
import com.lawmobile.domain.repository.user.UserRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {
    override suspend fun getUserFromNetwork(uuid: String): Result<User> {
        return try {
            val user = userRemoteDataSource.getUserFromNetwork(uuid).toDomain()
            println("User after success match Details: " + user.email + "," + user.id + "," + user.name + "," + user.devicePassword)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUserFromCamera(): Result<User> {
        return try {
            val user = userRemoteDataSource.getUserFromCamera().toDomain()
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
