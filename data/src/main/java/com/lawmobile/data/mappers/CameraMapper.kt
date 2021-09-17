package com.lawmobile.data.mappers

interface CameraMapper<T, Camera> {
    fun T.toCamera(): Camera
}
