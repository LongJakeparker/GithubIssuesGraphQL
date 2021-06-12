package com.example.githubissuesgraphql.custom

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Inherits RecyclerView.OnScrollListener to make recyclerView keep loading more data when reach bottom
 * @author longtran
 * @since 10/06/2021
 */
abstract class EndlessScrollListener(private val layoutManager: RecyclerView.LayoutManager) :
    RecyclerView.OnScrollListener() {

    // Flag to determine is fetching more data or not
    var isLoading = false
        private set

    // Number of prefetch items before reach at bottom
    private val visibleThreshold = 3

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (!isLoading // Only fetch when is not loading, if doesn't have this flag onLoadMore will be called multiple times
            && layoutManager.itemCount > 0
            && (findFirstVisibleItemPosition() + recyclerView.childCount + visibleThreshold) >= layoutManager.itemCount) {
            onLoadMore()
            isLoading = true
        }
    }

    /**
     * Sets flag to false after finished fetch data
     */
    fun setLoadingFinished() {
        isLoading = false
    }

    /**
     * Abstract function to notify the list need to load more items
     */
    abstract fun onLoadMore()

    private fun findFirstVisibleItemPosition(): Int {
        return (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }
}