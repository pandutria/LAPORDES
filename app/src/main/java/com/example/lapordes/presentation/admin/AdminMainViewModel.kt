package com.example.lapordes.presentation.admin

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lapordes.data.model.Complaint
import com.example.lapordes.data.repository.ComplaintRepository
import com.example.lapordes.data.state.ResultState

class AdminMainViewModel: ViewModel() {
    private val repository = ComplaintRepository()

    private val _getState = MutableLiveData<ResultState<List<Complaint>>>()
    val getState = _getState

    fun get(context: Context) {
        repository.get(context) {
            _getState.postValue(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }
}