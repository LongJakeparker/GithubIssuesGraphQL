package com.example.githubissuesgraphql.view.adapter

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubissuesgraphql.R
import com.example.githubissuesgraphql.databinding.ItemCommentBinding
import com.example.githubissuesgraphql.databinding.ItemHeaderIssueDetailBinding
import com.example.githubissuesgraphql.model.CommentModel
import com.example.githubissuesgraphql.model.IssueInfoModel
import com.example.githubissuesgraphql.type.IssueState

/**
 * Adapter to display the detail of issue
 * The issue detail includes the header view to display issue information
 * and a list of comments
 * @author longtran
 * @since 06/06/2021
 */
class IssueDetailAdapter : ListAdapter<
        IssueDetailDataList,
        RecyclerView.ViewHolder
        >(IssueDetailDataListDiffUtil()) {

    // A callback function to communicate with outside of the adapter
    var onItemClicked: (() -> Unit)? = null

    enum class IssueDetailViewType(val rawValue: Int) {
        HEADER(0), COMMENT(1)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> IssueDetailViewType.HEADER.rawValue

            else -> IssueDetailViewType.COMMENT.rawValue
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            IssueDetailViewType.HEADER.rawValue -> {
                val binding = ItemHeaderIssueDetailBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                HeaderViewHolder(binding)
            }

            else -> {
                val binding = ItemCommentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CommentViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is HeaderViewHolder -> if (item is IssueDetailDataList.HeaderData) holder.onBind(
                item.issueInfoModel,
                onItemClicked
            )

            is CommentViewHolder -> if (item is IssueDetailDataList.CommentData) holder.onBind(item.commentModel)
        }
    }

    class HeaderViewHolder(val binding: ItemHeaderIssueDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: IssueInfoModel?, onItemClicked: (() -> Unit)?) {
            binding.apply {
                issueInfo = item
                onMenuClick = View.OnClickListener {
                    showMenuPopup(item?.state, it, onItemClicked)
                }
                executePendingBindings()
            }
        }

        /**
         * Shows a menu popup to let owner close/reopen an issue
         * @param state
         * @param view
         * @param onItemClicked
         */
        private fun showMenuPopup(state: IssueState?, view: View, onItemClicked: (() -> Unit)?) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.issue_popup_menu, popup.menu)

            // Style the text view to fit with the current state of the issue
            // current state is OPEN means the menu allows Close issue and opposite
            if (state == IssueState.OPEN) {
                val coloredText =
                    SpannableString(view.context.getString(R.string.label_close_issue))
                coloredText.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(view.context, R.color.dark_red)),
                    0,
                    coloredText.length,
                    0
                )
                popup.menu[0].title = coloredText
            } else {
                val coloredText =
                    SpannableString(view.context.getString(R.string.label_reopen_issue))
                coloredText.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(view.context, R.color.dark_green)),
                    0,
                    coloredText.length,
                    0
                )
                popup.menu[0].title = coloredText
            }

            popup.setOnMenuItemClickListener {
                onItemClicked?.invoke()
                true
            }

            popup.show()
        }
    }

    class CommentViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: CommentModel?) {
            binding.apply {
                comment = item
                executePendingBindings()
            }
        }
    }

    /**
     * DiffUtil.ItemCallback of IssueDetailDataList
     */
    class IssueDetailDataListDiffUtil : DiffUtil.ItemCallback<IssueDetailDataList>() {

        override fun areItemsTheSame(
            oldItem: IssueDetailDataList,
            newItem: IssueDetailDataList
        ): Boolean {
            return when {
                oldItem is IssueDetailDataList.HeaderData && newItem is IssueDetailDataList.HeaderData -> {
                    oldItem.issueInfoModel?.number == newItem.issueInfoModel?.number
                }
                oldItem is IssueDetailDataList.CommentData && newItem is IssueDetailDataList.CommentData -> {
                    oldItem.commentModel?.createdAt == newItem.commentModel?.createdAt
                }
                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: IssueDetailDataList,
            newItem: IssueDetailDataList
        ): Boolean {
            return when {
                oldItem is IssueDetailDataList.HeaderData && newItem is IssueDetailDataList.HeaderData -> {
                    oldItem == newItem
                }
                oldItem is IssueDetailDataList.CommentData && newItem is IssueDetailDataList.CommentData -> {
                    oldItem == newItem
                }
                else -> false
            }
        }

    }
}

/**
 * A sealed class is used to merges multiple item type into the adapter data source
 */
sealed class IssueDetailDataList {
    class HeaderData(val issueInfoModel: IssueInfoModel?) : IssueDetailDataList()
    class CommentData(val commentModel: CommentModel?) : IssueDetailDataList()
}