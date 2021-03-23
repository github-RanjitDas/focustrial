package com.lawmobile.domain.enums

enum class CameraType {
    X1 {
        override fun getPossibleStringsToIdentifySSID(): List<String> {
            return listOf("X57")
        }
    },
    X2 {
        override fun getPossibleStringsToIdentifySSID(): List<String> {
            return listOf("FocusX2", "AmbaCam", "01")
        }
    };

    open fun getPossibleStringsToIdentifySSID(): List<String> = emptyList()
    open fun reviewIfIsThisTypeOfCamera(serialNumber: String): Boolean {
        getPossibleStringsToIdentifySSID().forEach {
            if (serialNumber.contains(it)) return true
        }
        return false
    }

    companion object {
        fun isValidNumberCameraBWC(codeCamera: String): Boolean =
            X1.reviewIfIsThisTypeOfCamera(codeCamera) || X2.reviewIfIsThisTypeOfCamera(codeCamera)

        fun getTypeOfCamera(serialNumber: String): CameraType {
            if (X1.reviewIfIsThisTypeOfCamera(serialNumber)) {
                return X1
            }
            return X2
        }
    }
}
