package com.example.githubissuesgraphql.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.githubissuesgraphql.Constant
import com.example.githubissuesgraphql.GithubIssuesApp
import com.example.githubissuesgraphql.SharedPreferencesManager
import com.example.githubissuesgraphql.custom.EndlessScrollListener
import com.example.githubissuesgraphql.databinding.FragmentIssuesListBinding
import com.example.githubissuesgraphql.model.IssueModel
import com.example.githubissuesgraphql.util.GeneralUtil
import com.example.githubissuesgraphql.view.adapter.IssueAdapter
import com.example.githubissuesgraphql.view.state.ViewState
import com.example.githubissuesgraphql.viewmodel.IssueViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/**
 * @author longtran
 * @since 06/06/2021
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class IssuesListFragment : BaseBindingFragment<FragmentIssuesListBinding>() {
    private val issueAdapter by lazy { IssueAdapter() }
    private val viewModel by viewModels<IssueViewModel>()

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    companion object {
        private const val REQUEST_KEY_ISSUE_CHANGED = "REQUEST_KEY_ISSUE_CHANGED"
        private const val REQUEST_KEY_CREATE_ISSUE = "REQUEST_KEY_CREATE_ISSUE"
    }

    // Listener for pull to refresh event
    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        // Only fetch when internet connection is available
        if (GeneralUtil.isNetworkAvailable(GithubIssuesApp.instance.applicationContext)) {
            fetchData()
        } else {
            binding.refreshLayout.isRefreshing = false
        }
    }

    // Listener for receiving bundle result from others fragment
    private val fragmentResultListener: (String, Bundle) -> Unit = { requestKey, bundle ->
        when (requestKey) {
            REQUEST_KEY_ISSUE_CHANGED -> {
                // Update list with issue contains new data
                viewModel.updateChangedIssue(bundle.getSerializable(Constant.EXTRA_KEY_ISSUE_CHANGED) as IssueModel)
            }

            REQUEST_KEY_CREATE_ISSUE -> {
                // Update list with new issue
                viewModel.addNewIssue(bundle.getSerializable(Constant.EXTRA_KEY_NEW_ISSUE) as IssueModel)
            }
        }
    }

    // Listener to notifies whenever user reach the bottom and need load more items
    private var endlessScrollListener: EndlessScrollListener? = null

    override val inflaterMethod: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIssuesListBinding
        get() = FragmentIssuesListBinding::inflate

    override fun onBindingReady() {
        binding.apply {
            viewModel = this@IssuesListFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setupRecyclerView()

        // Only load items when user is signed in
        if (sharedPreferencesManager.getAccessToken().isNotEmpty()) {
            // If internet connection is ok then fetch new data
            // else get data from local DB
            if (!GeneralUtil.isNetworkAvailable(GithubIssuesApp.instance.applicationContext)) {
                viewModel.queryIssueFromLocal()
            } else if (viewModel.issuesList.value == null) {
                fetchData()
            }
        }

        binding.refreshLayout.setOnRefreshListener(refreshListener)

        observeLiveData()

        setFragmentResultListener(REQUEST_KEY_CREATE_ISSUE, fragmentResultListener)
        setFragmentResultListener(REQUEST_KEY_ISSUE_CHANGED, fragmentResultListener)
    }

    /**
     * Navigates to issue detail
     * @param issue
     */
    private fun navigateToDetail(issue: IssueModel) {
        findNavController().navigate(
            IssuesListFragmentDirections.navigateToIssueDetailsFragment(
                id = issue.id,
                number = issue.number,
                title = issue.title ?: "",
                requestKey = REQUEST_KEY_ISSUE_CHANGED
            )
        )
    }

    /**
     * Setups all necessaries for recyclerView including adapter
     */
    private fun setupRecyclerView() {
        binding.rvIssues.apply {
            adapter = issueAdapter.apply {
                onItemClicked = { issue ->
                    navigateToDetail(issue)
                }
            }
            endlessScrollListener = object : EndlessScrollListener(layoutManager!!) {
                override fun onLoadMore() {
                    viewModel.queryIssuesList(isLoadMore = true)
                }
            }
            addOnScrollListener(endlessScrollListener!!)
        }
    }

    override fun fetchData() {
        viewModel.queryIssuesList()
    }

    /**
     * Observes all livedata in viewModel
     */
    private fun observeLiveData() {
        viewModel.apply {
            // The data flow of the issue list
            // listen to the state of the issue list data whether it has any changes
            issuesList.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Loading -> {
                        // Only show progress view when data is empty and not in refreshing
                        if (issueAdapter.itemCount == 0 && !binding.refreshLayout.isRefreshing) {
                            binding.apply {
                                rvIssues.visibility = View.GONE
                                tvIssuesEmpty.visibility = View.GONE
                                prgFetchIssues.visibility = View.VISIBLE
                            }
                        }
                    }
                    is ViewState.Success -> {
                        if (response.value?.size == 0) {
                            binding.apply {
                                rvIssues.visibility = View.GONE
                                tvIssuesEmpty.visibility = View.VISIBLE
                            }
                        } else {
                            binding.apply {
                                rvIssues.visibility = View.VISIBLE
                                tvIssuesEmpty.visibility = View.GONE
                            }
                        }
                        issueAdapter.submitList(response.value)
                        onFetchFinish()
                    }
                    is ViewState.Error -> {
                        viewModel.queryIssueFromLocal()
                        onFetchFinish()
                    }
                }
            }

            // LiveData to notifies the list has more data from server or not
            // if yes then set up for endless load feature on recyclerview
            // if no then remove that feature from recyclerView since it isn't necessary
            hasNextPage.observe(viewLifecycleOwner) { hasNextPage ->
                // Removes current listener
                endlessScrollListener?.let { binding.rvIssues.removeOnScrollListener(it) }
                // Sets flag for adapter to enable/disable endless scroll
                issueAdapter.enableLoadMore = hasNextPage

                if (hasNextPage) {
                    endlessScrollListener?.let { binding.rvIssues.addOnScrollListener(it) }
                    endlessScrollListener?.setLoadingFinished()
                } else {
                    issueAdapter.notifyItemRemoved(issueAdapter.itemCount)
                }
            }

            // LiveData to notifies user click start create new issue
            eventCreateIssue.observe(viewLifecycleOwner) {
                findNavController().navigate(
                    IssuesListFragmentDirections.navigateToCreateIssueFragment(
                        repositoryId = viewModel.repositoryId,
                        requestKey = REQUEST_KEY_CREATE_ISSUE
                    )
                )
            }
        }
    }

    /**
     * Do things that are needed after finished fetch new data
     */
    private fun onFetchFinish() {
        binding.refreshLayout.isRefreshing = false
        binding.prgFetchIssues.visibility = View.GONE
        endlessScrollListener?.setLoadingFinished()
    }
}