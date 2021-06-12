package com.example.githubissuesgraphql.view.state

/**
 * A sealed class to determine the state of the data flow
 * @author longtran
 * @since 06/06/2021
 */
sealed class ViewState<T>(
    val value: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : ViewState<T>(data)
    class Error<T>(message: String?, data: T? = null) : ViewState<T>(data, message)
    class Loading<T> : ViewState<T>()
}