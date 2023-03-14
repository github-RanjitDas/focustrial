package com.lawmobile.domain.enums

import com.lawmobile.domain.entities.CameraInfo

enum class CatalogTypes(val value: String) {
    EVENT("Event"), CATEGORIES("Categories");

    companion object {
        fun getSupportedCatalogType(): CatalogTypes {
            return if (CameraInfo.backOfficeType == BackOfficeType.NEXUS) {
                CATEGORIES
            } else {
                EVENT
            }
        }
    }
}
