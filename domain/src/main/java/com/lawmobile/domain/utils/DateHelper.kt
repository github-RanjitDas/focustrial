package com.lawmobile.domain.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateHelper {

    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private val formatWithHour = SimpleDateFormat("yyyy-MM-dd / hh:mm:ss", Locale.ENGLISH)

    fun dateToString(
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

    fun getCurrentDateWithHour(): String {
        return formatWithHour.format(System.currentTimeMillis())
    }
}
