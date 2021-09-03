package com.lawmobile.presentation.utils

import java.net.HttpURLConnection
import java.net.URL

class SimpleNetworkManager {

    fun verifyInternetConnection(callback: (Boolean) -> Unit) {
        try {
            val urlConnection = URL(URL).openConnection() as HttpURLConnection
            urlConnection.connectTimeout = CONNECT_TIMEOUT
            urlConnection.connect()
            callback(urlConnection.responseCode == SUCCESS_RESPONSE_CODE)
            urlConnection.disconnect()
        } catch (e: Exception) {
            callback(false)
        }
    }

    companion object {
        const val CONNECT_TIMEOUT = 3000
        const val SUCCESS_RESPONSE_CODE = 200
        private const val URL = "https://www.google.com"
    }
}
