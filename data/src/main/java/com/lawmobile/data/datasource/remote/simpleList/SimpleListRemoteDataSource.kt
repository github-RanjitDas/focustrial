package com.lawmobile.data.datasource.remote.simpleList

import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface SimpleListRemoteDataSource {
    suspend fun getSnapshotList(): Result<FileResponseWithErrors>
    suspend fun getVideoList(): Result<FileResponseWithErrors>
    suspend fun getAudioList(): Result<FileResponseWithErrors>
}
