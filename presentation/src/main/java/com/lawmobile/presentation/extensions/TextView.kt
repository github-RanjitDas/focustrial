package com.lawmobile.presentation.extensions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.TextView

fun TextView.showDateAndTimePickerDialog(
    hour: Int,
    minute: Int,
    callback: () -> Unit
) {
    val current = System.currentTimeMillis().convertMilliSecondsToDate().split("-")
    val pickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val date = fixDate(year, month + 1, dayOfMonth)
            text = date
            showTimePickerDialog(hour, minute)
            callback.invoke()
        },
        current[0].toInt(), current[1].toInt() - 1, current[2].toInt()
    )
    pickerDialog.show()
}

fun TextView.showTimePickerDialog(hour: Int, minute: Int) {
    val pickerDialog = TimePickerDialog(
        context,
        { _, hourSelected, minuteSelected ->
            val time = text.toString() + " " + fixTime(hourSelected, minuteSelected)
            text = time
        },
        hour, minute, true
    )

    pickerDialog.show()
}

private fun fixDate(year: Int, _month: Int, _day: Int): String {
    var month = _month.toString()
    var day = _day.toString()
    if (_month < 10) {
        month = "0$month"
    }
    if (_day < 10) {
        day = "0$day"
    }
    return "$year-$month-$day"
}

private fun fixTime(_hour: Int, _minute: Int): String {
    var minute = _minute.toString()
    var hour = _hour.toString()
    if (_minute < 10) {
        minute = "0$minute"
    }
    if (_hour < 10) {
        hour = "0$hour"
    }
    return "$hour:$minute"
}
