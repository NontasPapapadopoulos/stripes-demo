package com.example.stripesdemo.presentation.exception

import android.content.res.Resources
import android.util.Log

private const val unknownErrorTag: String = "Unknown Error: "

fun Resources.errorStringResource(throwable: Throwable): String {
    Log.e("error", "net", throwable)
    return when (throwable) {

        else -> throwable.message ?: (unknownErrorTag + throwable.javaClass.name)
    }
}