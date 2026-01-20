package com.example.lapordes.presentation.customer.complaint

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lapordes.data.model.Complaint
import com.example.lapordes.data.repository.ComplaintRepository
import com.example.lapordes.data.state.ResultState

class ComplaintViewModel: ViewModel() {
    private val repository = ComplaintRepository()

    private val _createState = MutableLiveData<ResultState<String>>()
    val createState = _createState

    fun create(
        title: String,
        category: String,
        priority: String,
        description: String,
        imageUrl: String,
        lat: Double,
        lng: Double,
        context: Context,
    ){
        repository.create(title, category, priority, description, imageUrl, lat, lng, context) {
            _createState.postValue(it)
        }
    }
}