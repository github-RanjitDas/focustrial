package com.lawmobile.domain.repository.typeOfCamera

import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface TypeOfCameraRepository : BaseRepository {
    suspend fun getTypeOfCamera(): Result<CameraType>
}
