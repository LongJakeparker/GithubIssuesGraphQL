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
import com.example.githubissuesgraphql.mapper.IssueMapper
import com.example.githubissuesgraphql.model.CommentModel
import com.example.githubissuesgraphql.model.IssueModel
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
class CreateIssueViewModel @Inject constructor(
    private val repository: IssuesRepository,
) : ViewModel() {

    //The data flows
    private val _newIssue by lazy { MutableLiveData<ViewState<IssueModel>>() }
    val newIssue: LiveData<ViewState<IssueModel>> = _newIssue

    //The single LiveData represents for click events and change property events
    private val _isEnableSubmit by lazy { MutableLiveData(false) }
    val isEnableSubmit: LiveData<Boolean> = _isEnableSubmit

    private val _isEnableLoading by lazy { MutableLiveData(false) }
    val isEnableLoading: LiveData<Boolean> = _isEnableLoading

    private val _eventSubmitIssue by lazy { SingleEventLiveData<Int>() }
    val eventSubmitIssue: LiveData<Int> = _eventSubmitIssue

    private val _eventCancel by lazy { SingleEventLiveData<Int>() }
    val eventCancel: LiveData<Int> = _eventCancel

    //On-click events are set to view through data-binding
    //Sets LiveData value to notify to Fragment/Activity
    val onClickSubmitIssue = View.OnClickListener {
        _eventSubmitIssue.setValue(1)
    }

    val onClickCancel = View.OnClickListener {
        _eventCancel.setValue(1)
    }

    val titleIssueTextWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            _isEnableSubmit.value = s?.trim()?.length!! > 0
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
     * Creates new issue
     * @param repositoryId
     * @param title
     * @param comment
     */
    fun createNewIssue(repositoryId: String, title: String, comment: String) = viewModelScope.launch {
        // Start loading
        _newIssue.postValue(ViewState.Loading())
        try {
            val response = repository.createNewIssue(
                repositoryId,
                title,
                comment
            )

            // Check if there is any error occur
            if (response.errors.isNullOrEmpty()) {
                // Parse data and post value to view
                val responseData = response.data?.createIssue?.issue?.let {
                    IssueMapper.transformNewIssue(
                        it
                    )
                }
                _newIssue.postValue(ViewState.Success(responseData ?: IssueModel()))
            } else {
                // Handles error
                _newIssue.postValue(ViewState.Error(response.errors?.get(0)?.message))
            }
        } catch (e: Exception) {
            // Handles exception
            _newIssue.postValue(ViewState.Error(e.message))
        }
    }

}