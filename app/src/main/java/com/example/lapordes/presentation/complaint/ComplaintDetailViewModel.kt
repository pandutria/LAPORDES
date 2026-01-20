package com.example.lapordes.presentation.complaint

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lapordes.data.model.Comment
import com.example.lapordes.data.repository.CommentRepository
import com.example.lapordes.data.repository.ComplaintRepository
import com.example.lapordes.data.state.ResultState

class ComplaintDetailViewModel: ViewModel() {
    private val commentRepository = CommentRepository()
    private val complaintRepository = ComplaintRepository()

    private val _createState = MutableLiveData<ResultState<String>>()
    val createState = _createState

    private val _getState = MutableLiveData<ResultState<List<Comment>>>()
    val getState = _getState

    private val _updateState = MutableLiveData<ResultState<String>>()
    val updateState = _updateState

    fun create(
        comment: String,
        complaint_uid: String,
        context: Context,
    ) {
        commentRepository.create(comment, complaint_uid, context) {
            _createState.postValue(it)
        }
    }

    fun get(complaint_uid: String) {
        commentRepository.get(complaint_uid) {
            _getState.postValue(it)
        }
    }

    fun updateStatus(complaint_uid: String, status: String, note: String) {
        complaintRepository.changeStatus(complaint_uid, status, note) {
            _updateState.postValue(it)
        }
    }
}