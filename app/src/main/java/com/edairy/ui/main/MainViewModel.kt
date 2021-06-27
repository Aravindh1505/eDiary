package com.edairy.ui.main

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.edairy.utils.AppConstants
import com.edairy.utils.Logger
import com.edairy.utils.internet.Connectivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Firebase.firestore

    var networkConnected = ObservableField("")
    var speed = ObservableField("")
    var mobileNumber = ObservableField("")
    var timeStamp = ObservableField("")

    var isInternetConnected = ObservableField(false)
    var networkSpeed = ObservableField("")


    var visibilityOfProgress = ObservableField(View.GONE)
    var visibilityOfButton = ObservableField(View.VISIBLE)

    private var _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private var _refresh = MutableLiveData<Boolean>()
    val refresh: LiveData<Boolean> = _refresh

    init {
        timeStamp.set(AppConstants.getCurrentDate())
    }

    fun onRefresh() {
        _refresh.value = true
    }

    fun onSubmit() {
        checkValidation()
    }

    private fun checkValidation() {
        val isConnected = Connectivity.isConnected(getApplication())
        val mobileNumber = mobileNumber.get().toString()

        isInternetConnected.set(isConnected)
        Logger.d(isConnected.toString())

        return when {
            !isConnected -> {
                _message.value = "Internet is not connected please check your internet connection"
            }
            mobileNumber.isEmpty() && mobileNumber.length != 10 -> {
                _message.value = "Please enter valid mobile number"
            }
            else -> {
                showProgress(true)
                addData()
            }
        }
    }


    private fun addData() {
        FirebaseAnalytics.getInstance(getApplication()).setUserId(mobileNumber.get().toString())

        db.collection(mobileNumber.get().toString())
            .add(createData())
            .addOnSuccessListener { documentReference ->
                Logger.d("DocumentSnapshot added with ID: ${documentReference.id}")
                showProgress(false)
                _message.value = "Data added successfully"
            }
            .addOnFailureListener { e ->
                Logger.d("Error adding document : ${e.message}")
                showProgress(false)
            }
    }

    private fun createData(): HashMap<String, String> {
        return hashMapOf(
            "isNetworkConnected" to isInternetConnected.get().toString(),
            "speed" to networkSpeed.get().toString(),
            "timestamp" to timeStamp.get().toString(),
            "mobile" to mobileNumber.get().toString()
        )
    }

    private fun showProgress(isShow: Boolean) {
        if (isShow) {
            visibilityOfProgress.set(View.VISIBLE)
            visibilityOfButton.set(View.GONE)
        } else {
            visibilityOfProgress.set(View.GONE)
            visibilityOfButton.set(View.VISIBLE)
        }
    }

    fun resetMessage() {
        _message.value = ""
    }

    fun resetRefresh() {
        _refresh.value = false
    }

}