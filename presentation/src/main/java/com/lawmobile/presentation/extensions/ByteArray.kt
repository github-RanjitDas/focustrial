package com.lawmobile.presentation.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun ByteArray.convertBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}