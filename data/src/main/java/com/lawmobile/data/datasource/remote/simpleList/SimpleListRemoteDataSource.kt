package com.lawmobile.data.datasource.remote.simpleList

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.commons.helpers.Result

interface SimpleListRemoteDataSource {
    suspend fun getSnapshotList(): Result<CameraConnectFileResponseWithErrors>
    suspend fun getVideoList(): Result<CameraConnectFileResponseWithErrors>
}