package com.lawmobile.domain.entities

data class DomainInformationImage(
    override val domainCameraFile: DomainCameraFile,
    var imageBytes: ByteArray? = null,
    override var isSelected: Boolean = false,
    var internalPath: String? = null
) : DomainInformationForList {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DomainInformationImage

        other.imageBytes?.let { otherBytes ->
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
        return 0
    }
}