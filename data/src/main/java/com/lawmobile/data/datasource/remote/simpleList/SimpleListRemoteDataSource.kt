package com.lawmobile.data.datasource.remote.simpleList

import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors

interface SimpleListRemoteDataSource {
    suspend fun getSnapshotList(): Result<FileResponseWithErrors>
    suspend fun getVideoList(): Result<FileResponseWithErrors>
}