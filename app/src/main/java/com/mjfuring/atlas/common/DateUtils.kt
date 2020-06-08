package com.mjfuring.atlas.common

import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

fun Long.toStringDate(): String {
    try {
        if (this > 0L) {
            val date = Date(this)
            val format = SimpleDateFormat("MM-dd-yyyy h:mm a", Locale.getDefault())
            return format.format(date)
        }
    } catch (ex: Throwable) {
        Timber.e(ex)
    }
    return ""
}