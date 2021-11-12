package com.lawmobile.data.extensions

import com.lawmobile.data.dto.interceptors.RequestInterceptor
import io.ktor.client.HttpClient

fun HttpClient.addInterceptor(interceptor: RequestInterceptor) {
    interceptor.start(this)
}
