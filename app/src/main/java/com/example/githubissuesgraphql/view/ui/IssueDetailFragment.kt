package com.example.githubissuesgraphql.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.githubissuesgraphql.Constant
import com.example.githubissuesgraphql.GithubIssuesApp
import com.example.githubissuesgraphql.databinding.FragmentIssueDetailBinding
import com.example.githubissuesgraphql.mapper.IssueMapper
import com.example.githubissuesgraphql.model.CommentModel
import com.example.githubissuesgraphql.model.IssueInfoModel
import com.example.githubissuesgraphql.util.GeneralUtil
import com.example.githubissuesgraphql.view.adapter.IssueDetailAdapter
import com.example.githubissuesgraphql.view.adapter.IssueDetailDataList
import com.example.githubissuesgraphql.view.state.ViewState
import com.example.githubissuesgraphql.viewmodel.IssueDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * @author longtran
 * @since 06/06/2021
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class IssueDetailFragment : BaseBindingFragment<FragmentIssueDetailBinding>() {
    private val args: IssueDetailFragmentArgs by navArgs()
    private val viewModel by viewModels<IssueDetailViewModel>()
    private val issueDetailAdapter by lazy { IssueDetailAdapter() }

    companion object {
        private const val REQUEST_KEY_ADD_COMMENT = "REQUEST_KEY_ADD_COMMENT"
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
    private val fragmentResultListener: (String, Bundle) -> Unit = { _, bundle ->
        // Update list comment by adding new comment
        val newComment = bundle.getSerializable(Constant.EXTRA_KEY_NEW_COMMENT) as CommentModel
        val newList = ArrayList(issueDetailAdapter.currentList)
        newList.add(IssueDetailDataList.CommentData(newComment))
        issueDetailAdapter.submitList(newList)

        binding.rvContent.smoothScrollToPosition(issueDetailAdapter.itemCount)
    }

    override val inflaterMethod: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIssueDetailBinding
        get() = FragmentIssueDetailBinding::inflate

    override fun getDynamicTitle(): String {
        return args.title
    }

    override fun onBindingReady() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@IssueDetailFragment.viewModel
        }

        binding.refreshLayout.setOnRefreshListener(refreshListener)
        setUpRecyclerView()

        // Only fetch data when internet connection is available
        if (!GeneralUtil.isNetworkAvailable(GithubIssuesApp.instance.applicationContext)) {
            binding.tvDetailEmpty.visibility = View.VISIBLE
        } else {
            fetchData()
        }

        observeLiveData()

        setFragmentResultListener(REQUEST_KEY_ADD_COMMENT, fragmentResultListener)
    }

    private fun setUpRecyclerView() {
        binding.rvContent.adapter = issueDetailAdapter.apply {
            onItemClicked = {
                if (GeneralUtil.isNetworkAvailable(requireContext())) {
                    viewModel.changeStateIssue()
                    setResult(viewModel.issueDetail?.info)
                }
            }
        }
    }

    /**
     * Sets the issue with new data so IssuesListFragment can update new issue
     * @param issue
     */
    private fun setResult(issue: IssueInfoModel?) {
        setFragmentResult(
            args.requestKey,
            Bundle().apply {
                putSerializable(
                    Constant.EXTRA_KEY_ISSUE_CHANGED,
                    IssueMapper.transformIssueInfo(issue ?: IssueInfoModel())
                )
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTranslationZ(binding.root, 100f)
    }

    override fun fetchData() {
        viewModel.queryIssuesDetail(args.number)
    }

    /**
     * Observes all livedata in viewModel
     */
    private fun observeLiveData() {
        viewModel.apply {
            // The data flow of the issue detail
            // listen to the state of the issue detail data whether its get succeed or not
            issuesDetailList.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Loading -> {
                        if (!binding.refreshLayout.isRefreshing) {
                            binding.apply {
                                prgFetchDetail.visibility = View.VISIBLE
                                tvDetailEmpty.visibility = View.GONE
                            }
                        }
                    }
                    is ViewState.Success -> {
                        binding.apply {
                            refreshLayout.isRefreshing = false
                            prgFetchDetail.visibility = View.GONE
                            tvDetailEmpty.visibility = View.GONE
                        }
                        if (!response.value?.isNullOrEmpty()!!) {
                            issueDetailAdapter.submitList(response.value)
                            binding.cvComment.visibility = View.VISIBLE
                        }
                    }
                    is ViewState.Error -> {
                        binding.apply {
                            refreshLayout.isRefreshing = false
                            tvDetailEmpty.visibility = View.VISIBLE
                            prgFetchDetail.visibility = View.GONE
                            cvComment.visibility = View.GONE
                        }
                    }
                }
            }

            // LiveData to notifies user click start add new comment
            eventAddComment.observe(viewLifecycleOwner) {
                findNavController().navigate(
                    IssueDetailFragmentDirections.navigateToAddCommentFragment(
                        issueId = args.id,
                        requestKey = REQUEST_KEY_ADD_COMMENT
                    )
                )
            }
        }
    }
}