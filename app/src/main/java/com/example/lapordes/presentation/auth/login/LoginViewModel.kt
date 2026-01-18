package com.example.lapordes.presentation.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lapordes.data.model.User
import com.example.lapordes.data.repository.AuthRepository
import com.example.lapordes.data.state.ResultState

class LoginViewModel: ViewModel() {
    private val repository = AuthRepository()

    private val _loginState = MutableLiveData<ResultState<User>>()
    val loginState = _loginState

    fun login(email: String, password: String) {
        repository.login(email, password) {
            _loginState.postValue(it)
        }
    }
}