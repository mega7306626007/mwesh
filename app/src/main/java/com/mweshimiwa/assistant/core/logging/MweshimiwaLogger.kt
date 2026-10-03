package com.mweshimiwa.assistant.core.logging

import android.util.Log

object MweshimiwaLogger {
    private const val TAG = "MWESHIMIWA"

    fun d(message: String) = Log.d(TAG, message)
    fun i(message: String) = Log.i(TAG, message)
    fun w(message: String) = Log.w(TAG, message)
    fun e(message: String, throwable: Throwable? = null) = Log.e(TAG, message, throwable)
}
