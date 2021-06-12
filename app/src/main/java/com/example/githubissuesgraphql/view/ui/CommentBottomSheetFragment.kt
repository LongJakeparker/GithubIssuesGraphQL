package com.example.githubissuesgraphql.view.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.githubissuesgraphql.Constant
import com.example.githubissuesgraphql.R
import com.example.githubissuesgraphql.databinding.FragmentBottomSheetCommentBinding
import com.example.githubissuesgraphql.util.GeneralUtil
import com.example.githubissuesgraphql.view.state.ViewState
import com.example.githubissuesgraphql.viewmodel.CommentViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Inherits BottomSheetDialogFragment to display an edittext to add comment
 * @author longtran
 * @since 30/05/2021
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CommentBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetCommentBinding
    private val viewModel by viewModels<CommentViewModel>()
    private val args: CommentBottomSheetFragmentArgs by navArgs()

    companion object {
        private const val DIALOG_HEIGHT_PERCENTAGE = 0.8
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetCommentBinding.inflate(inflater, container, false).apply {
            viewModel = this@CommentBottomSheetFragment.viewModel
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

            // LiveData to notifies user click save comment
            eventSaveComment.observe(viewLifecycleOwner) {
                val content = binding.etCommentContent.text.toString().trim()
                if (content.isNotEmpty()) {
                    addCommentToIssue(args.issueId, content)
                    GeneralUtil.hideKeyboardFrom(requireContext(), binding.root)
                }
            }

            // The data flow of the new comment
            // listen to the state of the new comment whether its posted succeed or not
            newComment.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Loading -> {
                        setEnableLoading(true)
                    }

                    is ViewState.Success -> {
                        setEnableLoading(false)

                        // Sets the fragment result for IssueDetailFragment to adds new comment directly
                        setFragmentResult(
                            args.requestKey,
                            Bundle().apply {
                                putSerializable(
                                    Constant.EXTRA_KEY_NEW_COMMENT,
                                    response.value
                                )
                            })
                        dismiss()
                    }

                    is ViewState.Error -> {
                        setEnableLoading(false)
                        GeneralUtil.showMessage(requireContext(), response.message)
                    }
                }
            }

            eventCancel.observe(viewLifecycleOwner) {
                dismiss()
            }
        }
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