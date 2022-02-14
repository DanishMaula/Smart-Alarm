package com.danish.smartalarm.helper

import java.text.SimpleDateFormat
import java.util.*


fun timeFormatter(hour: Int, minute: Int): String{
    val calendar = Calendar.getInstance()
    calendar.set(0, 0, 0, hour, minute)
    val timeFormatted = SimpleDateFormat( "HH:mm", Locale.getDefault())

    return timeFormatted.format(calendar.time)

}