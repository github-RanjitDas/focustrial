package com.lawmobile.data.mappers

interface LocalMapper<T, Local> {
    fun T.toLocal(): Local
}
