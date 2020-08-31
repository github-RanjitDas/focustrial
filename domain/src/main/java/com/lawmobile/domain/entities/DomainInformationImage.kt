package com.lawmobile.domain.entities

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile

data class DomainInformationImage(
    val cameraConnectFile: CameraConnectFile,
    var imageBytes: ByteArray? = null,
    var isAssociatedToVideo: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DomainInformationImage

        other.imageBytes?.let {otherBytes ->
            imageBytes?.let { currentBytes ->
                if (currentBytes.contentEquals(otherBytes)) return true
            }
        }

        return true
    }

    override fun hashCode(): Int {
        imageBytes?.let {
            return it.contentHashCode()
        }
        return  0
    }
}