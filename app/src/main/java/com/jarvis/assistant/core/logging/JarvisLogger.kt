package com.jarvis.assistant.core.logging

import android.util.Log

object JarvisLogger {
    private const val TAG = "JARVIS"

    fun d(message: String) = Log.d(TAG, message)
    fun i(message: String) = Log.i(TAG, message)
    fun w(message: String) = Log.w(TAG, message)
    fun e(message: String, throwable: Throwable? = null) = Log.e(TAG, message, throwable)
}
