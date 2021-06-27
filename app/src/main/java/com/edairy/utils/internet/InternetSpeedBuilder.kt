package com.edairy.utils.internet

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError


data class ProgressionModel(var speed: Double = 0.0)

interface OnEventInternetSpeedListener {
    fun onSpeedTest(progressModel: ProgressionModel)
    fun onTestComplete()
}


@Suppress("DEPRECATION")
class InternetSpeedBuilder(var activity: Activity) {

    private var limit = 3
    lateinit var url: String
    lateinit var listener: OnEventInternetSpeedListener
    private lateinit var progressModel: ProgressionModel

    fun start(url: String, limitCount: Int) {
        this.url = url
        this.limit = limitCount
        startTestDownload()
    }

    fun setOnEventInternetSpeedListener(javaListener: OnEventInternetSpeedListener) {
        this.listener = javaListener
    }

    private fun startTestDownload() {
        progressModel = ProgressionModel()
        SpeedTestTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class SpeedTestTask : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void): String? {

            val speedTestSocket = SpeedTestSocket()
            speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {

                override fun onCompletion(report: SpeedTestReport) {
                    // called when download/upload is finished
                    Log.d("onCompletion", "report = ${report.progressPercent}")
                    listener.onTestComplete()
                }

                override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                    // called when a download/upload error occur
                    listener.onTestComplete()
                    Log.v("onError", "errorMessage = $errorMessage")
                }

                override fun onProgress(percent: Float, report: SpeedTestReport) {
                    progressModel.speed = report.transferRateBit.toDouble()
                    activity.runOnUiThread {
                        listener.onSpeedTest(progressModel)
                    }
                }
            })

            speedTestSocket.startDownload(url)
            return null
        }
    }
}