package com.example.githubissuesgraphql.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubissuesgraphql.custom.SingleEventLiveData
import com.example.githubissuesgraphql.mapper.IssueMapper
import com.example.githubissuesgraphql.model.IssueModel
import com.example.githubissuesgraphql.repository.IssuesRepository
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
class IssueViewModel @Inject constructor(
    private val repository: IssuesRepository,
) : ViewModel() {

    private var endCursor = ""
    private var currentList = ArrayList<IssueModel>()
    var repositoryId = ""
        private set

    //The data flows
    private val _hasNextPage by lazy { MutableLiveData(false) }
    val hasNextPage: LiveData<Boolean>
        get() = _hasNextPage

    private val _issuesList by lazy { MutableLiveData<ViewState<ArrayList<IssueModel>>>() }
    val issuesList: LiveData<ViewState<ArrayList<IssueModel>>>
        get() = _issuesList

    //The single LiveData represents for click events and change property events
    private val _eventCreateIssue by lazy { SingleEventLiveData<Int>() }
    val eventCreateIssue: LiveData<Int> = _eventCreateIssue

    //On-click events are set to view through data-binding
    //Sets LiveData value to notify to Fragment/Activity
    val clickCreateIssueListener = View.OnClickListener {
        _eventCreateIssue.setValue(1)
    }

    /**
     * Queries the issue list
     * @param isLoadMore - determines this query is the first load or load more items
     */
    fun queryIssuesList(isLoadMore: Boolean = false) = viewModelScope.launch {
        // Start loading
        _issuesList.postValue(ViewState.Loading())
        try {

            val response = if (isLoadMore) {
                repository.queryIssuesList(endCursor)
            } else {
                repository.queryIssuesList()
            }

            // Check if there is any error occur
            if (response.errors.isNullOrEmpty()) {
                response.data?.repository?.id?.let { repositoryId = it }
                val responseData = response.data?.repository?.issues

                // Parse data and post value to view
                val listIssues = IssueMapper.transformCollection(responseData?.nodes)

                if (!isLoadMore) {
                    currentList = ArrayList(listIssues)
                } else {
                    currentList.addAll(listIssues)
                }

                // Posts data to view and update to Room DB
                updateList(isLoadMore)

                // Save information for the next load
                responseData?.pageInfo?.endCursor?.let { endCursor = it }
                _hasNextPage.postValue(responseData?.pageInfo?.hasNextPage ?: false)
            } else {
                // Handles error
                _issuesList.postValue(ViewState.Error(response.errors?.get(0)?.message))
            }
        } catch (e: Exception) {
            // Handles exception
            _issuesList.postValue(ViewState.Error(e.message))
        }
    }

    /**
     * Update new data to Room DB and post data to view
     */
    private fun updateList(isLoadMore: Boolean = false) = viewModelScope.launch {
        if (repository.getLocalIssues().isNotEmpty() && !isLoadMore) {
            repository.removeIssues()
        }

        repository.insertIssues(ArrayList(IssueMapper.transformCollectionModelToEntity(currentList)))
        _issuesList.postValue(ViewState.Success(ArrayList(currentList)))
    }

    /**
     * Adds new issue to current list
     * @param issue
     */
    fun addNewIssue(issue: IssueModel) {
        currentList.add(0, issue)
        updateList()
    }

    /**
     * Queries issue list from Room DB
     */
    fun queryIssueFromLocal() = viewModelScope.launch {
        try {
            val response = IssueMapper.transformCollectionEntityToModel(repository.getLocalIssues())
            _issuesList.postValue(ViewState.Success(ArrayList(response)))
        } catch (e: Exception) {}
    }

    /**
     * Updates new data to an exist issue
     * @param issue
     */
    fun updateChangedIssue(issue: IssueModel) {
        currentList.indexOfFirst { item -> item.id == issue.id }.let {
            if (it > 0) {
                currentList[it] = issue
                updateList()
            }
        }
    }
}