package com.servicebio.compose.application.ext

import android.net.Uri

fun String.decodeUri(): String {
    return Uri.decode(this)
}

fun String.encodeUri(): String {
    return Uri.encode(this)
}