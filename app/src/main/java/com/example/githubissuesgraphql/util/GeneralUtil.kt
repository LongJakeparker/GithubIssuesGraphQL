package com.example.githubissuesgraphql.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.icu.text.CaseMap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.example.githubissuesgraphql.type.ReactionContent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


/**
 * @author longtran
 * @since 07/06/2021
 */
object GeneralUtil {
    fun getEmoji(content: ReactionContent?): String {
        return when (content) {
            ReactionContent.THUMBS_UP -> "\uD83D\uDC4D"
            ReactionContent.THUMBS_DOWN -> "\uD83D\uDC4E"
            ReactionContent.LAUGH -> "\uD83D\uDE04"
            ReactionContent.HOORAY -> "\uD83C\uDF89"
            ReactionContent.CONFUSED -> "\uD83D\uDE15"
            ReactionContent.HEART -> "❤️"
            ReactionContent.ROCKET -> "\uD83D\uDE80"
            ReactionContent.EYES -> "\uD83D\uDC40"

            else -> ""
        }
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    fun showMessage(context: Context, message: String?) {
        if (message.isNullOrEmpty())
            return

        AlertDialog.Builder(context)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    fun showOptionMessage(context: Context, title: Int, message: Int?, onclick: DialogInterface.OnClickListener) {
        if (message == null)
            return

        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("ok") { dialog, which ->
                onclick.onClick(dialog, which)
            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}