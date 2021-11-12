package com.lawmobile.data.repository.user

import com.lawmobile.data.datasource.remote.user.UserRemoteDataSource
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class UserRepositoryImplTest {

    private val dataSource: UserRemoteDataSource = mockk()
    private val userRepositoryImpl = UserRepositoryImpl(dataSource)

    @Test
    fun getUserFromNetworkSuccess() = runBlocking {
        coEvery { dataSource.getUserFromNetwork(any()) } returns mockk(relaxed = true)
        val result = userRepositoryImpl.getUserFromNetwork("")
        Assert.assertTrue(result is Result.Success)
        coVerify { dataSource.getUserFromNetwork(any()) }
    }

    @Test
    fun getUserFromNetworkError() = runBlocking {
        coEvery { dataSource.getUserFromNetwork(any()) } throws Exception()
        val result = userRepositoryImpl.getUserFromNetwork("")
        Assert.assertTrue(result is Result.Error)
        coVerify { dataSource.getUserFromNetwork(any()) }
    }

    @Test
    fun getUserFromCameraSuccess() = runBlocking {
        coEvery { dataSource.getUserFromCamera() } returns mockk(relaxed = true)
        val result = userRepositoryImpl.getUserFromCamera()
        Assert.assertTrue(result is Result.Success)
        coVerify { dataSource.getUserFromCamera() }
    }

    @Test
    fun getUserFromCameraError() = runBlocking {
        coEvery { dataSource.getUserFromCamera() } throws Exception()
        val result = userRepositoryImpl.getUserFromCamera()
        Assert.assertTrue(result is Result.Error)
        coVerify { dataSource.getUserFromCamera() }
    }
}
