package com.lawmobile.body_cameras.x1.entities

data class X1FileResponse(
    val rval: Int = 0,
    val token: Int = 0,
    val type: String = "",
    val msg_id: String? = "",
    val param: List<Map<String, Any>>? = null,
    val size: Long? = null
) {
    fun getBytesSent(): Long {
        val sizeFile = (param?.first()?.get("bytes sent") as? Double)?.toLong() ?: size ?: 0
        println("getBytesSent Size file: $sizeFile")
        return sizeFile
    }

    fun isCommandSuccess(): Boolean =
        rval == X1CameraResponse.SUCCESS_RESPONSE || rval == X1CameraResponse.SUCCESS_RESPONSE_SECOND_POSSIBILITY

    fun isInvalidParam(): Boolean =
        rval == X1CameraResponse.ERROR_INVALID_PARAM

    fun isInvalidPath(): Boolean =
        rval == X1CameraResponse.ERROR_INVALID_PATH
}
