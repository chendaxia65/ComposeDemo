package com.servicebio.compose.application.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.servicebio.compose.application.MainApplication


fun showToast(message: Any, duration: Int = Toast.LENGTH_SHORT) {
    val context = MainApplication.context.get() ?: return
    ToastUtils.showToast(context, message, duration)
}

object ToastUtils {
    private var lastMessage = ""
    private var lastShowTime = 0L
    private val mainHandler = Handler(Looper.getMainLooper())

    private var toast: Toast? = null

    fun showToast(context: Context, message: Any, duration: Int = Toast.LENGTH_SHORT) {
        val text = when (message) {
            is String -> message
            is Int -> context.getString(message)
            else -> message.toString()
        }

        if (lastMessage == text && System.currentTimeMillis() - lastShowTime < 2000L) return

        lastShowTime = System.currentTimeMillis()
        lastMessage = text

        mainHandler.post {
            toast?.cancel()
            toast = Toast.makeText(context, text, duration)
            toast?.show()
        }
    }
}