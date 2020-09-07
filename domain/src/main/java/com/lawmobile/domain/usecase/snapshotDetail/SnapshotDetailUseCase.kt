package com.lawmobile.domain.usecase.snapshotDetail

import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface SnapshotDetailUseCase : BaseUseCase {
    suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray>
    suspend fun savePartnerIdSnapshot(cameraFile: CameraConnectFile,partnerId: String): Result<Unit>
    suspend fun getInformationOfPhoto(cameraFile: CameraConnectFile): Result<DomainInformationImageMetadata>
}