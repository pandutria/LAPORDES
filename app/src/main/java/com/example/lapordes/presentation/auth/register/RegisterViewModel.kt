package com.example.lapordes.presentation.auth.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lapordes.data.repository.AuthRepository
import com.example.lapordes.data.state.ResultState

class RegisterViewModel: ViewModel() {
    private val repository = AuthRepository()

    private val _registerState = MutableLiveData<ResultState<String>>()
    val registerState get()  = _registerState

    fun register(email: String, password: String) {
        repository.register(email, password) {
            _registerState.postValue(it)
        }
    }
}