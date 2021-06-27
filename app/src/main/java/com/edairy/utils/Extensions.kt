package com.edairy.utils

import android.widget.Toast
import androidx.fragment.app.Fragment


fun Fragment.showToast(message: String?) {
    if (!message.isNullOrEmpty()) {
        Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
    }
}