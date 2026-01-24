package com.example.lapordes.presentation.customer.profile.update

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lapordes.data.model.User
import com.example.lapordes.data.repository.AuthRepository
import com.example.lapordes.data.state.ResultState
import com.google.firebase.Timestamp

class ProfileUpdateViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _meState = MutableLiveData<ResultState<User>>()
    val meState = _meState

    private val _updateState = MutableLiveData<ResultState<User>>()
    val updateState = _updateState

    fun me(context: Context) {
        authRepository.me(context) {
            _meState.postValue(it)
        }
    }

    fun update(
        context: Context,
        email: String,
        password: String,
        image: String,
        fullname: String,
        username: String,
        phone: String,
        nik: String,
        gender: String,
        birth: Timestamp,
    ) {
        authRepository.updateProfile(
            context,
            email,
            password,
            image,
            fullname,
            username,
            phone,
            nik,
            gender,
            birth
        ) {
            _updateState.postValue(it)
        }
    }
}