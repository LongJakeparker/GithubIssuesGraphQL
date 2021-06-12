package com.example.githubissuesgraphql.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubissuesgraphql.custom.SingleEventLiveData
import com.example.githubissuesgraphql.mapper.IssueDetailMapper
import com.example.githubissuesgraphql.model.IssueDetailModel
import com.example.githubissuesgraphql.repository.IssuesRepository
import com.example.githubissuesgraphql.type.IssueState
import com.example.githubissuesgraphql.view.adapter.IssueDetailDataList
import com.example.githubissuesgraphql.view.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author longtran
 * @since 06/06/2021
 */
@ExperimentalCoroutinesApi
@HiltViewModel
class IssueDetailViewModel @Inject constructor(
    private val repository: IssuesRepository,
) : ViewModel() {

    // Data of issue detail
    var issueDetail: IssueDetailModel? = null
    private set

    //The data flows
    private val _issuesDetailList by lazy { MutableLiveData<ViewState<ArrayList<IssueDetailDataList>>>() }
    val issuesDetailList: LiveData<ViewState<ArrayList<IssueDetailDataList>>>
        get() = _issuesDetailList

    //The single LiveData represents for click events and change property events
    private val _eventAddComment by lazy { SingleEventLiveData<Int>() }
    val eventAddComment: LiveData<Int> = _eventAddComment

    //On-click events are set to view through data-binding
    //Sets LiveData value to notify to Fragment/Activity
    val clickAddCommentListener = View.OnClickListener {
        _eventAddComment.setValue(1)
    }

    /**
     * Changes state of an issue
     * OPEN -> CLOSE and opposite
     */
    fun changeStateIssue() {
        viewModelScope.launch {
            try {
                if (issueDetail?.info?.state == IssueState.OPEN) {
                    issueDetail?.info?.id?.let { repository.closeIssue(it) }
                } else {
                    issueDetail?.info?.id?.let { repository.reopenIssue(it) }
                }
            } catch (e: Exception) {}
        }

        // Change the state of the current model
        issueDetail?.info?.state = if (issueDetail?.info?.state == IssueState.OPEN) IssueState.CLOSED else IssueState.OPEN
        issueDetail?.let { _issuesDetailList.postValue(ViewState.Success(mergeResponse(it))) }
    }

    /**
     * Queries the issue detail
     * @param number - the number of issue
     */
    fun queryIssuesDetail(number: Int) = viewModelScope.launch {
        // Start loading
        _issuesDetailList.postValue(ViewState.Loading())
        try {
            val response = repository.queryIssueDetail(number)

            // Check if there is any error occur
            if (response.errors.isNullOrEmpty()) {
                // Parse data and post value to view
                issueDetail = IssueDetailMapper.transform(response.data?.repository?.issue)
                _issuesDetailList.postValue(ViewState.Success(mergeResponse(issueDetail!!)))
            } else {
                // Handles error
                _issuesDetailList.postValue(ViewState.Error(response.errors?.get(0)?.message))
            }
        } catch (e: Exception) {
            // Handles exception
            _issuesDetailList.postValue(ViewState.Error(e.message))
        }
    }

    /**
     * Merges multiple data type into a consistent data type array list
     * @param response - issue detail model
     * @return ArrayList<IssueDetailDataList>
     */
    private fun mergeResponse(response: IssueDetailModel): ArrayList<IssueDetailDataList> {
        val result = ArrayList<IssueDetailDataList>()
        result.add(IssueDetailDataList.HeaderData(response.info))

        response.comments?.forEach { comment -> result.add(IssueDetailDataList.CommentData(comment)) }
        return result
    }
}