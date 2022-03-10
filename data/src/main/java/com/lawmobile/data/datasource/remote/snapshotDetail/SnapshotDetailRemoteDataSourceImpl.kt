package com.lawmobile.data.datasource.remote.snapshotDetail

import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.PhotoInformation
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result

class SnapshotDetailRemoteDataSourceImpl(cameraServiceFactory: CameraServiceFactory) :
    SnapshotDetailRemoteDataSource {

    private var cameraService = cameraServiceFactory.create()

    override suspend fun getImageBytes(cameraFile: CameraFile): Result<ByteArray> =
        cameraService.getImageBytes(cameraFile)

    override suspend fun savePartnerIdSnapshot(photoInformation: PhotoInformation): Result<Unit> =
        cameraService.savePhotoMetadata(photoInformation)

    override suspend fun getInformationOfPhoto(cameraFile: CameraFile): Result<PhotoInformation> =
        cameraService.getPhotoMetadata(cameraFile)

    override suspend fun savePartnerIdInAllSnapshots(list: List<PhotoInformation>): Result<Unit> =
        cameraService.saveAllPhotoMetadata(list)

    override suspend fun getSavedPhotosMetadata(): Result<List<PhotoInformation>> =
        cameraService.getMetadataOfPhotos()
}
