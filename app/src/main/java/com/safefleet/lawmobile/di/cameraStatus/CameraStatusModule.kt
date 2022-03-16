package com.safefleet.lawmobile.di.cameraStatus

import com.lawmobile.data.datasource.remote.bodyCameraStatus.BodyCameraStatusDataSource
import com.lawmobile.data.datasource.remote.bodyCameraStatus.BodyCameraStatusDataSourceImpl
import com.lawmobile.data.repository.bodyCameraStatus.BodyCameraStatusRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.repository.bodyCameraStatus.BodyCameraStatusRepository
import com.lawmobile.domain.usecase.checkCameraRecordingVideo.CheckCameraRecordingVideo
import com.lawmobile.domain.usecase.checkCameraRecordingVideo.CheckCameraRecordingVideoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object CameraStatusModule {
    @Provides
    fun provideCheckCameraRecording(
        bodyCameraStatusRepository: BodyCameraStatusRepository
    ): CheckCameraRecordingVideo = CheckCameraRecordingVideoImpl(bodyCameraStatusRepository)

    @Provides
    fun provideBodyCameraStatusRepository(
        bodyCameraStatusDataSource: BodyCameraStatusDataSource
    ): BodyCameraStatusRepository = BodyCameraStatusRepositoryImpl(bodyCameraStatusDataSource)

    @Provides
    fun provideBodyCameraStatusDataSource(
        bodyCameraFactory: CameraServiceFactory
    ): BodyCameraStatusDataSource = BodyCameraStatusDataSourceImpl(bodyCameraFactory)
}
