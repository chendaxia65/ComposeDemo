package com.servicebio.compose.application.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimestampUtils {


    private val formatterYear = createFormatter("yyyy-MM-dd HH:mm")
    private val formatterWeek = createFormatter("MM-dd HH:mm")
    private val formatterDay = createFormatter("HH:mm")

    private val formatterSeek = createFormatter("mm:ss")

    fun formatMessageDate(date: Long): String {
        try {
            val rightNow = Calendar.getInstance()
            val day = rightNow[Calendar.DAY_OF_YEAR] //当前的天数
            val year = rightNow[Calendar.YEAR] //当前的年
            rightNow.timeInMillis = date
            val dateDay = rightNow[Calendar.DAY_OF_YEAR] //消息的天数
            val dateYear = rightNow[Calendar.YEAR] //消息的年

            return if (dateDay == day && year == dateYear) { //当天的时间格式化
                formatterDay.format(Date(date))
            } else if (dateDay + 1 == day && year == dateYear) { //昨天
                "昨日 " + formatterDay.format(Date(date))
            } else if (year == dateYear) { //一周之内
                formatterWeek.format(Date(date))
            } else {
                formatterYear.format(Date(date))
            }
        } catch (e: java.lang.Exception) {
        }
        return "LOC_ERR: formatDate"
    }

    private fun createFormatter(format: String): SimpleDateFormat {
        return SimpleDateFormat(format, Locale.getDefault())
    }
}