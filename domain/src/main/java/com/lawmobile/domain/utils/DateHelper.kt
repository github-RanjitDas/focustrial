package com.lawmobile.domain.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateHelper {

    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    private fun dateToString(
        date: Date,
        pattern: String = "dd/MM/yyyy",
        locale: Locale = Locale.getDefault()
    ): String {
        val format = SimpleDateFormat(pattern, locale)
        return format.format(date)
    }

    fun getTodayDateAtStartOfTheDay(): String {
        return format.format(System.currentTimeMillis()) + " 00:00:00"
    }

    fun getCurrentDate(): String {
        return dateToString(Date(), NOTIFICATION_DATE_PATTERN)
    }

    private const val NOTIFICATION_DATE_PATTERN = "yyyy-MM-dd / HH:mm:ss"
}
