package com.edairy.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.edairy.BuildConfig


object Logger {

    private val TAG = Logger::class.java.simpleName

    private var className: String? = null
    private var methodName: String? = null
    private var lineNumber: Int = 0

    private val isDebugMode: Boolean
        get() = BuildConfig.DEBUG

    private fun createLog(log: String): String {
        return if (!isDebugMode) {
            ""
        } else "[" + className!!.replace(
            ".java",
            ""
        ) + " - " + methodName + " - " + lineNumber + "] - " + log
    }

    private fun getMethodNames(sElements: Array<StackTraceElement>) {
        if (isDebugMode) {
            className = sElements[1].fileName
            methodName = sElements[1].methodName
            lineNumber = sElements[1].lineNumber
        }
    }

    fun d(data: String?) {
        data?.let {
            getMethodNames(Throwable().stackTrace)
            Log.d(TAG, createLog(data))
        }
    }

    fun w(data: String) {
        getMethodNames(Throwable().stackTrace)
        Log.w(TAG, createLog(data))
    }

    fun v(data: String) {
        getMethodNames(Throwable().stackTrace)
        Log.v(TAG, createLog(data))
    }

    fun i(data: String) {
        getMethodNames(Throwable().stackTrace)
        Log.i(TAG, createLog(data))
    }

    fun e(data: String) {
        getMethodNames(Throwable().stackTrace)
        Log.e(TAG, createLog(data))
    }

    fun showMessage(context: Context?, message: String?) {
        if (isDebugMode) {
            getMethodNames(Throwable().stackTrace)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
