package com.lawmobile.domain.entities

enum class CameraType {
    X1 {
        override fun getStringToIdentifySSID(): String {
            return "X57"
        }

        override fun reviewIfIsThisTypeOfCamera(serialNumber: String): Boolean {
            return serialNumber.contains(getStringToIdentifySSID())
        }
    },
    X2 {
        override fun getStringToIdentifySSID(): String {
            return "FocusX2"
        }

        override fun reviewIfIsThisTypeOfCamera(serialNumber: String): Boolean {
            return serialNumber.contains(getStringToIdentifySSID())
        }
    };

    open fun getStringToIdentifySSID(): String = ""
    open fun reviewIfIsThisTypeOfCamera(serialNumber: String): Boolean = false

    companion object {
        fun isValidNumberCameraBWC(codeCamera: String): Boolean =
            codeCamera.contains(X1.getStringToIdentifySSID()) ||
                codeCamera.contains(X2.getStringToIdentifySSID())

        fun getTypeOfCamera(serialNumber: String): CameraType {
            if (X1.reviewIfIsThisTypeOfCamera(serialNumber)) {
                return X1
            }
            return X2
        }
    }
}
