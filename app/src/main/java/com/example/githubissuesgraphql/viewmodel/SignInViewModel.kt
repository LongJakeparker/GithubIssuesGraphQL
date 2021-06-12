package com.example.githubissuesgraphql.viewmodel

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.githubissuesgraphql.Constant
import com.example.githubissuesgraphql.SharedPreferencesManager
import com.example.githubissuesgraphql.custom.SingleEventLiveData
import com.example.githubissuesgraphql.model.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.zze
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.Exception
import javax.inject.Inject


/**
 * @author longtran
 * @since 08/06/2021
 */
@ExperimentalCoroutinesApi
@HiltViewModel
class SignInViewModel @Inject constructor(
    val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    //The single LiveData represents for click events and change property events
    private val _eventSignInSuccess by lazy { SingleEventLiveData<Int>() }
    val eventSignInSuccess: LiveData<Int> = _eventSignInSuccess

    private val _eventSignInFail by lazy { SingleEventLiveData<Exception>() }
    val eventSignInFail: LiveData<Exception> = _eventSignInFail

    /**
     * Starts sign in process with github
     * @param activity
     */
    fun startSignInProcess(activity: Activity) {
        val pendingResultTask = Firebase.auth.pendingAuthResult
        //You should first check if you've already received a response.
        // Signing in via this method puts your Activity in the background,
        // which means that it can be reclaimed by the system during the sign in flow.
        // In order to make sure that you don't make the user try again if this happens,
        // you should check if a result is already present.
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            handlePendingResultTask(pendingResultTask)
        } else {
            // There's no pending result so you need to start the sign-in flow.
            signIn(activity)
        }
    }

    /**
     * Handles pending result task
     * @param pendingResultTask
     */
    private fun handlePendingResultTask(pendingResultTask: Task<AuthResult>) {
        pendingResultTask
            .addOnSuccessListener { result ->
                // User is signed in.
                onSignInSuccess(result)
            }
            .addOnFailureListener {
                // Handle failure.
                _eventSignInFail.setValue(it)
            }
    }

    /**
     * Sign in with github
     * @param activity
     */
    private fun signIn(activity: Activity) {
        // Defines scope 'public_repo' so user can make mutations with public repos
        val provider = OAuthProvider.newBuilder(Constant.GITHUB_PROVIDE_ID).apply {
            scopes = arrayListOf("user:email", "public_repo")
        }
        Firebase.auth
            .startActivityForSignInWithProvider(activity, provider.build())
            .addOnSuccessListener { result ->
                // User is signed in.
                onSignInSuccess(result)
            }
            .addOnFailureListener {
                // Handle failure.
                _eventSignInFail.setValue(it)
            }
    }

    /**
     * Handles on sign in success
     * Saves user information to local and posts event to view
     * @param result
     */
    private fun onSignInSuccess(result: AuthResult?) {
        val userModel = UserModel(
            result?.additionalUserInfo?.profile?.get(Constant.KEY_LOGIN)?.toString(),
            result?.additionalUserInfo?.profile?.get(Constant.KEY_AVATAR_URL)?.toString()
        )
        sharedPreferencesManager.putSignInData(
            userModel,
            (result?.credential as zze).accessToken ?: ""
        )
        _eventSignInSuccess.setValue(1)
    }

}