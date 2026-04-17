package com.stampcollect.util

import java.text.SimpleDateFormat
import java.util.*

enum class DateFormat(val pattern: String) {
    DEFAULT("dd MMM yyyy"),
    WITH_TIME("dd MMM yyyy, HH:mm"),
    SHORT("dd/MM/yy"),
    RELIABLE_FS("yyyy-MM-dd-HH-mm-ss-SSS"),
    MONTH_ONLY("MMMM"),
    MONTH_YEAR("MMMM yyyy");

    fun format(timestamp: Long): String {
        return SimpleDateFormat(pattern, Locale.US).format(Date(timestamp))
    }
}
