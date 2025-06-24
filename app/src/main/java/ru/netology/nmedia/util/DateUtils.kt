package ru.netology.nmedia.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun now(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())
    }
}