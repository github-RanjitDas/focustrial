package com.lawmobile.domain.enums

enum class CameraType {
    X2 {
        override fun getPossibleStringsToIdentifySSID(): List<String> {
            return listOf("FocusX2", "AmbaCam", "01", "X0", "X2")
        }
    };

    fun isX2() = this == X2

    open fun getPossibleStringsToIdentifySSID(): List<String> = emptyList()

    open fun reviewIfIsThisTypeOfCamera(serialNumber: String): Boolean {
        getPossibleStringsToIdentifySSID().forEach {
            if (serialNumber.contains(it)) return true
        }
        return false
    }

    companion object {
        fun isValidBodyCameraNumber(codeCamera: String): Boolean =
             X2.reviewIfIsThisTypeOfCamera(codeCamera)
    }
}
