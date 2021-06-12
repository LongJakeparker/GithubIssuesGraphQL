package com.example.githubissuesgraphql.custom

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import com.example.githubissuesgraphql.R
import com.example.githubissuesgraphql.databinding.ItemReactionBinding
import com.example.githubissuesgraphql.model.ReactionModel
import com.example.githubissuesgraphql.type.IssueState
import com.example.githubissuesgraphql.util.GeneralUtil
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author longtran
 * @since 09/06/2021
 */

@BindingAdapter("android:avatarUrl")
fun setAvatarUrl(imageView: ImageView, url: String?) {
    imageView.load(url) {
        crossfade(true)
        transformations(CircleCropTransformation())
    }
}

@BindingAdapter("android:stateImage")
fun setStateImage(imageView: ImageView, state: IssueState?) {
    val resource = when (state) {
        IssueState.OPEN -> {
            R.drawable.ic_issue_open
        }

        else -> {
            R.drawable.ic_issue_close
        }
    }
    imageView.setImageResource(resource)
}

@BindingAdapter("android:stateLabel")
fun setStateLabel(textView: TextView, state: IssueState?) {
    when (state) {
        IssueState.OPEN -> {
            textView.apply {
                setTextColor(ContextCompat.getColor(textView.context, R.color.sup_dark_green))
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_issue_open_small, 0, 0, 0)
                setBackgroundResource(R.drawable.bg_state_open)
                text = context.getString(R.string.label_open)
            }
        }

        else -> {
            textView.apply {
                setTextColor(ContextCompat.getColor(textView.context, R.color.dark_red))
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_issue_close_small, 0, 0, 0)
                setBackgroundResource(R.drawable.bg_state_close)
                text = context.getString(R.string.label_closed)
            }
        }
    }
}

@BindingAdapter("android:reactions")
fun displayReactions(container: ViewGroup, reactions: List<ReactionModel>?) {
    container.removeAllViews()
    if (reactions.isNullOrEmpty()) {
        return
    }

    reactions.forEach { reaction ->
        if (reaction.totalCount != null && reaction.totalCount!! > 0) {
            val reactionView = ItemReactionBinding.inflate(LayoutInflater.from(container.context))

            reactionView.tvReactionContent.text = container.context.getString(
                R.string.format_reaction_content,
                reaction.emoji,
                reaction.totalCount
            )
            container.addView(reactionView.root)
        }
    }
}

@BindingAdapter("android:textWatcher")
fun setTextWatcher(view: EditText, textWatcher: TextWatcher) {
    view.addTextChangedListener(textWatcher)
}

@BindingAdapter("android:viewVisible")
fun setViewVisible(view: View, visible: Boolean) {
    if (visible) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("android:visibleEmptyText")
fun setVisibleEmptyText(view: View, body: String?) {
    if (body.isNullOrEmpty()) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("android:dateIssue")
fun setDateIssue(view: TextView, date: String) {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    val calendar = Calendar.getInstance()

    try {
        calendar.time = sdf.parse(date) ?: Date()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    val returnFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    view.text =
        view.context.getString(R.string.format_issue_create_at, returnFormat.format(calendar.time))
}

@BindingAdapter("android:dateComment")
fun setDateComment(view: TextView, date: String) {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    val calendar = Calendar.getInstance()

    try {
        calendar.time = sdf.parse(date) ?: Date()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    val returnFormat = SimpleDateFormat("MMM dd, yy HH:mm", Locale.US)
    view.text = returnFormat.format(calendar.time)
}