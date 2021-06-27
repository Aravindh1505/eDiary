package com.edairy.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.edairy.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object AppConstants {


    /*FIREBASE*/
    var MOBILE_NUMBER = ""


    const val URL =
        "https://www.learningcontainer.com/wp-content/uploads/2020/07/Large-Sample-Image-download-for-Testing.jpg"

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a")
        val currentDate = sdf.format(Date())
        println(" C DATE is  $currentDate")
        return currentDate
    }


    private fun formatFileSize(size: Double): String {
        val hrSize: String
        val k = size / 1024.0
        val m = size / 1024.0 / 1024.0
        val g = size / 1024.0 / 1024.0 / 1024.0
        val t = size / 1024.0 / 1024.0 / 1024.0 / 1024.0
        val dec = DecimalFormat("0.00")
        hrSize = if (t > 1) {
            dec.format(t) + " "
        } else if (g > 1) {
            dec.format(g)
        } else if (m > 1) {
            dec.format(m) + " mb/s"
        } else if (k > 1) {
            dec.format(k) + " kb/s"
        } else {
            dec.format(size)
        }
        return hrSize
    }

    fun getFormattedValue(speed: Double): String {
        return formatFileSize(speed)
    }

    fun startAnimation(view: ImageView?, context: Context) {
        val aniRotate: Animation = AnimationUtils.loadAnimation(
            context,
            R.anim.rotate
        )
        view?.startAnimation(aniRotate)
    }


}