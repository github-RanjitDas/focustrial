package com.safefleet.lawmobile.di.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSourceImpl
import com.lawmobile.data.repository.fileList.FileListRepositoryImpl
import com.lawmobile.domain.repository.fileList.FileListRepository
import com.lawmobile.domain.usecase.fileList.FileListUseCase
import com.lawmobile.domain.usecase.fileList.FileListUseCaseImpl
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import dagger.Module
import dagger.Provides

@Module
class FileListModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideFileListRemoteDataSource(cameraConnectService: CameraConnectService): FileListRemoteDataSource =
            FileListRemoteDataSourceImpl(cameraConnectService)

        @JvmStatic
        @Provides
        fun provideFileListRepository(fileListRemoteDataSource: FileListRemoteDataSource): FileListRepository =
            FileListRepositoryImpl(fileListRemoteDataSource)

        @JvmStatic
        @Provides
        fun provideFileListUseCase(fileListRepository: FileListRepository): FileListUseCase =
            FileListUseCaseImpl(fileListRepository)
    }
}