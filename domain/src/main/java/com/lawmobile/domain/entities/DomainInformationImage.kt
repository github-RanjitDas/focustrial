package com.lawmobile.domain.entities

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile

data class DomainInformationImage(
    val cameraConnectFile: CameraConnectFile,
    val imageBytes: ByteArray,
    var isAssociatedToVideo: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DomainInformationImage

        if (!imageBytes.contentEquals(other.imageBytes)) return false

        return true
    }

    override fun hashCode(): Int {
        return imageBytes.contentHashCode()
    }
}