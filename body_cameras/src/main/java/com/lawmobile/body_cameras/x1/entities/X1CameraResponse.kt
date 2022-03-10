package com.lawmobile.body_cameras.x1.entities

import com.lawmobile.body_cameras.entities.CameraFile

data class X1CameraResponse(
    val rval: Int? = null,
    val msg_id: Int? = null,
    val param: String? = null,
    private val listing: List<Map<String, String>>? = null
) {
    fun getItemsFile(path: String, folder: String): List<CameraFile> {

        val items =
            listing?.map { CameraFile(it.keys.first(), it.values.first(), path, folder) }
                ?: emptyList()
        println("getItemsFile items: " + path + folder + " : " + items.size.toString())
        return items
    }

    fun isCommandSuccess(): Boolean =
        rval == SUCCESS_RESPONSE || rval == SUCCESS_RESPONSE_SECOND_POSSIBILITY

    fun isTokenInvalid() = rval == ERROR_IN_TOKEN

    companion object {
        const val SUCCESS_RESPONSE = 0
        const val ERROR_INVALID_PARAM = -25
        const val ERROR_INVALID_PATH = -26
        const val SUCCESS_RESPONSE_SECOND_POSSIBILITY = -1
        const val ERROR_IN_TOKEN = -4
    }
}
