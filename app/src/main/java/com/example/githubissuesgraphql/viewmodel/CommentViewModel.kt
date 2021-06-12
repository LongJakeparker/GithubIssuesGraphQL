package com.example.githubissuesgraphql.viewmodel

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.exception.ApolloException
import com.example.githubissuesgraphql.custom.SingleEventLiveData
import com.example.githubissuesgraphql.mapper.CommentMapper
import com.example.githubissuesgraphql.model.CommentModel
import com.example.githubissuesgraphql.repository.IssuesRepository
import com.example.githubissuesgraphql.view.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

/**
 * @author longtran
 * @since 09/06/2021
 */
@ExperimentalCoroutinesApi
@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: IssuesRepository,
) : ViewModel() {

    //The data flows
    private val _newComment by lazy { MutableLiveData<ViewState<CommentModel>>() }
    val newComment: LiveData<ViewState<CommentModel>> = _newComment

    //The single LiveData represents for click events and change property events
    private val _isEnableSave by lazy { MutableLiveData(false) }
    val isEnableSave: LiveData<Boolean> = _isEnableSave

    private val _isEnableLoading by lazy { MutableLiveData(false) }
    val isEnableLoading: LiveData<Boolean> = _isEnableLoading

    private val _eventSaveComment by lazy { SingleEventLiveData<Int>() }
    val eventSaveComment: LiveData<Int> = _eventSaveComment

    private val _eventCancel by lazy { SingleEventLiveData<Int>() }
    val eventCancel: LiveData<Int> = _eventCancel

    //On-click events are set to view through data-binding
    //Sets LiveData value to notify to Fragment/Activity
    val onClickSaveComment = View.OnClickListener {
        _eventSaveComment.setValue(1)
    }

    val onClickCancel = View.OnClickListener {
        _eventCancel.setValue(1)
    }

    val commentTextWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            _isEnableSave.value = s?.trim()?.length!! > 0
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

    /**
     * Sets enable/disable loading view
     * @param enable
     */
    fun setEnableLoading(enable: Boolean) {
        _isEnableLoading.value = enable
    }

    /**
     * Adds new comment to issue
     * @param issueId
     * @param body
     */
    fun addCommentToIssue(issueId: String, body: String) = viewModelScope.launch {
        // Start loading
        _newComment.postValue(ViewState.Loading())
        try {
            val response = repository.addCommentToIssue(
                issueId,
                body
            )

            // Check if there is any error occur
            if (response.errors.isNullOrEmpty()) {
                // Parse data and post value to view
                val responseData = CommentMapper.transform(response.data?.addComment?.commentEdge?.node)
                _newComment.postValue(ViewState.Success(responseData))
            } else {
                // Handles error
                _newComment.postValue(ViewState.Error(response.errors?.get(0)?.message))
            }
        } catch (e: Exception) {
            // Handles exception
            _newComment.postValue(ViewState.Error(e.message))
        }
    }

}