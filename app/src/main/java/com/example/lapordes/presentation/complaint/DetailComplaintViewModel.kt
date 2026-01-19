package com.example.lapordes.presentation.complaint

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lapordes.data.model.Comment
import com.example.lapordes.data.repository.CommentRepository
import com.example.lapordes.data.state.ResultState

class DetailComplaintViewModel: ViewModel() {
    private val commentRepository = CommentRepository()

    private val _createState = MutableLiveData<ResultState<String>>()
    val createState = _createState

    private val _getState = MutableLiveData<ResultState<List<Comment>>>()
    val getState = _getState

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
}