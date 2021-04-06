package com.lawmobile.data.datasource.remote.bodyWornDiagnosis

import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result

class BodyWornDiagnosisDataSourceImpl(cameraServiceFactory: CameraServiceFactory) :
    BodyWornDiagnosisDataSource {

    private val cameraService = cameraServiceFactory.create()

    override suspend fun isDiagnosisSuccess(): Result<Boolean> {
        return cameraService.getBodyWornDiagnosis()
    }
}
