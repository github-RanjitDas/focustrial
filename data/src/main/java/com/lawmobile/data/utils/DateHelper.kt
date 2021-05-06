package com.lawmobile.data.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateHelper {
    fun dateToString(
        date: Date,
        pattern: String = "dd/MM/yyyy",
        locale: Locale = Locale.getDefault()
    ): String {
        val format = SimpleDateFormat(pattern, locale)
        return format.format(date)
    }
}
