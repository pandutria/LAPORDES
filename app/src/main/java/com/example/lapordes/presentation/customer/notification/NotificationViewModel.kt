package com.example.lapordes.presentation.customer.notification

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lapordes.data.model.Notification
import com.example.lapordes.data.repository.NotificationRepository
import com.example.lapordes.data.state.ResultState

class NotificationViewModel: ViewModel() {
    private val repository = NotificationRepository()

    private val _getState = MutableLiveData<ResultState<List<Notification>>>()
    val getState = _getState

    fun get(user_uid: String) {
        repository.getNotif(user_uid) {
            _getState.postValue(it)
        }
    }
}