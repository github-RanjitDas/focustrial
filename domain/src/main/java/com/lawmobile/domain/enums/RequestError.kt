package com.lawmobile.domain.enums

enum class RequestError(val code: Int) {
    GET_LIST(1),
    GET_METADATA(2),
    TIMEOUT(3),
    UNKNOWN(0);

    fun getException(): Exception {
        return Exception(name)
    }

    companion object {
        private fun getError(exception: Exception): RequestError {
            return when (exception.message) {
                GET_LIST.name -> GET_LIST
                GET_METADATA.name -> GET_METADATA
                TIMEOUT.name -> TIMEOUT
                else -> UNKNOWN
            }
        }

        fun getErrorMessage(message: String, exception: Exception): String {
            val error = getError(exception)
            return message + " [${error.code}]"
        }
    }
}
