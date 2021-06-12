package com.example.githubissuesgraphql.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.githubissuesgraphql.databinding.ItemIssueBinding
import com.example.githubissuesgraphql.databinding.ItemLoadingBinding
import com.example.githubissuesgraphql.model.IssueModel

/**
 * Adapter to display list of issues
 * @author longtran
 * @since 06/06/2021
 */
class IssueAdapter : ListAdapter<
        IssueModel,
        RecyclerView.ViewHolder
        >(IssueDiffUtil()) {

    enum class IssueViewType(val rawValue: Int) {
        ITEM(0), LOAD_MORE(1)
    }

    // Flag to enable/disable load more feature on recyclerView
    var enableLoadMore = false

    // A callback function to communicate with outside of the adapter
    var onItemClicked: ((IssueModel) -> Unit)? = null

    override fun getItemCount(): Int {
        return currentList.size + if (enableLoadMore) 1 else 0 // list size + load more item
    }

    override fun getItemViewType(position: Int): Int {
        return if (enableLoadMore && position == itemCount - 1) {
            IssueViewType.LOAD_MORE.rawValue
        } else {
            IssueViewType.ITEM.rawValue
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            IssueViewType.LOAD_MORE.rawValue -> {
                val binding = ItemLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LoadingViewHolder(binding)
            }

            else -> {
                val binding = ItemIssueBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                IssueViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is IssueViewHolder) {
            holder.binding.issue = getItem(position)
            val issue = getItem(position)
            holder.binding.root.setOnClickListener {
                onItemClicked?.invoke(issue)
            }
        }
    }

    class IssueViewHolder(val binding: ItemIssueBinding) :
        RecyclerView.ViewHolder(binding.root)

    class LoadingViewHolder(val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)


    /**
     * DiffUtil.ItemCallback of IssueModel
     */
    class IssueDiffUtil : DiffUtil.ItemCallback<IssueModel>() {

        override fun areItemsTheSame(oldItem: IssueModel, newItem: IssueModel): Boolean {
            return oldItem.number == newItem.number
        }

        override fun areContentsTheSame(oldItem: IssueModel, newItem: IssueModel): Boolean {
            return oldItem == newItem
        }

    }
}