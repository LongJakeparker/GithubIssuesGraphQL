package com.example.githubissuesgraphql.view.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.githubissuesgraphql.Constant
import com.example.githubissuesgraphql.R
import com.example.githubissuesgraphql.databinding.FragmentBottomSheetCreateIssueBinding
import com.example.githubissuesgraphql.model.IssueModel
import com.example.githubissuesgraphql.util.GeneralUtil
import com.example.githubissuesgraphql.view.state.ViewState
import com.example.githubissuesgraphql.viewmodel.CreateIssueViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Inherits BottomSheetDialogFragment to display edittext to create issue
 * @author longtran
 * @since 30/05/2021
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CreateIssueBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetCreateIssueBinding
    private val viewModel by viewModels<CreateIssueViewModel>()
    private val args: CreateIssueBottomSheetFragmentArgs by navArgs()

    companion object {
        private const val DIALOG_HEIGHT_PERCENTAGE = 0.8
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetCreateIssueBinding.inflate(inflater, container, false).apply {
            viewModel = this@CreateIssueBottomSheetFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        observeEvents()

        return binding.root
    }

    /**
     * Observes all livedata in viewModel
     */
    private fun observeEvents() {
        viewModel.apply {

            // LiveData to notifies user click submit new issue
            eventSubmitIssue.observe(viewLifecycleOwner) {
                val title = binding.etIssueTitle.text.toString().trim()
                if (title.isNotEmpty()) {
                    val comment = binding.etIssueComment.text.toString().trim()
                    createNewIssue(args.repositoryId, title, comment)
                    GeneralUtil.hideKeyboardFrom(requireContext(), binding.root)
                }
            }

            // The data flow of the new issue
            // listen to the state of the new issue whether its created succeed or not
            newIssue.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Loading -> {
                        setEnableLoading(true)
                    }

                    is ViewState.Success -> {
                        setEnableLoading(false)
                        // Sets the fragment result for IssuesListFragment to adds new issue directly
                        setResult(response.value)
                        response.value?.let { navigateToIssueDetail(it) }
                        dismiss()
                    }

                    is ViewState.Error -> {
                        setEnableLoading(false)
                        GeneralUtil.showMessage(requireContext(), response.message)
                    }
                }
            }

            // LiveData to notifies user click cancel
            eventCancel.observe(viewLifecycleOwner) {
                dismiss()
            }
        }
    }

    /**
     * Navigates to issue detail after create succeeded
     * @param issue
     */
    private fun navigateToIssueDetail(issue: IssueModel) {
        findNavController().navigate(
            CreateIssueBottomSheetFragmentDirections.navigateToIssueDetailsFragment(
                id = issue.id,
                number = issue.number,
                title = issue.title ?: "",
                ""
            )
        )
    }

    private fun setResult(issue: IssueModel?) {
        setFragmentResult(
            args.requestKey,
            Bundle().apply {
                putSerializable(
                    Constant.EXTRA_KEY_NEW_ISSUE,
                    issue
                )
            })
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        dialog.setOnShowListener { dialogResult ->
            // Styling for the dialog
            // Sets dialog background
            val bottomSheet =
                (dialogResult as BottomSheetDialog).findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(R.drawable.bg_bottom_sheet)

            // Sets dialog expanded when appear
            bottomSheet?.let {
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }

            // Sets height of dialog
            bottomSheet?.layoutParams?.height =
                (Resources.getSystem().displayMetrics.heightPixels * DIALOG_HEIGHT_PERCENTAGE).toInt()
            bottomSheet?.requestLayout()
        }
    }
}