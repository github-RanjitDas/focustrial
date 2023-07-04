package net.safefleet.focus.di.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSourceImpl
import com.lawmobile.data.repository.fileList.FileListRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.repository.fileList.FileListRepository
import com.lawmobile.domain.usecase.fileList.FileListUseCase
import com.lawmobile.domain.usecase.fileList.FileListUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class FileListModule {

    companion object {
        @Provides
        fun provideFileListRemoteDataSource(cameraService: CameraServiceFactory): FileListRemoteDataSource =
            FileListRemoteDataSourceImpl(cameraService)

        @Provides
        fun provideFileListRepository(fileListRemoteDataSource: FileListRemoteDataSource): FileListRepository =
            FileListRepositoryImpl(fileListRemoteDataSource)

        @Provides
        fun provideFileListUseCase(fileListRepository: FileListRepository): FileListUseCase =
            FileListUseCaseImpl(fileListRepository)
    }
}
