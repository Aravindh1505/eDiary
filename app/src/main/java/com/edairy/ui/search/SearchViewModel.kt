package com.edairy.ui.search

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.edairy.utils.AppConstants
import com.edairy.utils.Logger
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Firebase.firestore
    var mobileNumber = ObservableField("")

    private var _searchModelResponse = MutableLiveData<ArrayList<SearchModel>>()
    val searchModelResponse: LiveData<ArrayList<SearchModel>> = _searchModelResponse

    var visibilityOfProgress = ObservableField(View.GONE)
    var visibilityOfMessage = ObservableField(View.GONE)
    var visibilityOfRecyclerView = ObservableField(View.VISIBLE)

    init {
        val mobile = AppConstants.MOBILE_NUMBER
        if (mobile.isNotEmpty()) {
            mobileNumber.set(mobile)
            if (mobile.length == 10) {
                getSearchData(mobile)
            }
        }
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        Logger.d("onTextChanged $s")
        getDataFromServer(s)
    }

    private fun getDataFromServer(s: CharSequence) {
        if (s.length == 10) {
            visibilityOfProgress.set(View.VISIBLE)
            visibilityOfRecyclerView.set(View.GONE)
            visibilityOfMessage.set(View.GONE)
            getSearchData(s.toString())
        }
    }

    private fun getSearchData(mobileNumber: String) {
        val dataRef = db.collection(mobileNumber)

        dataRef.orderBy("timestamp", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result ->
                processData(result)
            }
            .addOnFailureListener { exception ->
                Logger.d("Error getting documents : ${exception.message}")
            }
    }

    private fun processData(result: QuerySnapshot) {
        val searchList = arrayListOf<SearchModel>()

        for (document in result) {
            Logger.d("document : ${document.data["timestamp"].toString()}")

            val search = SearchModel(
                id = document.id,
                isNetworkConnected = document.data["isNetworkConnected"].toString(),
                speed = document.data["speed"].toString(),
                timestamp = document.data["timestamp"].toString(),
                mobile = document.data["mobile"].toString()
            )

            searchList.add(search)
        }

        Logger.d(searchList.size.toString())

        if (searchList.isNotEmpty()) {
            _searchModelResponse.value = searchList

            visibilityOfMessage.set(View.GONE)
            visibilityOfProgress.set(View.GONE)
            visibilityOfRecyclerView.set(View.VISIBLE)
        } else {
            visibilityOfMessage.set(View.VISIBLE)
            visibilityOfProgress.set(View.GONE)
            visibilityOfRecyclerView.set(View.GONE)
        }
    }

}