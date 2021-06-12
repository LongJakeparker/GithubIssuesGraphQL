package com.example.githubissuesgraphql.view.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.githubissuesgraphql.Constant
import com.example.githubissuesgraphql.R
import com.example.githubissuesgraphql.databinding.ActivitySignInBinding
import com.example.githubissuesgraphql.viewmodel.SignInViewModel
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * @author longtran
 * @since 08/06/2021
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val viewModel by viewModels<SignInViewModel>()

    companion object {
        fun startForResult(activity: Activity, launcher: ActivityResultLauncher<Intent>) {
            val intent = Intent(activity, SignInActivity::class.java)
            launcher.launch(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnSignIn.setOnClickListener {
            viewModel.startSignInProcess(this)
        }

        // Build spannable string contains clickable link to Terms of use and privacy policy
        makePrivacyPolicyLink()

        observeEvents()
    }

    /**
     * Observes all livedata in viewModel
     */
    private fun observeEvents() {
        viewModel.apply {
            // LiveData to notifies user signed in succeed
            eventSignInSuccess.observe(this@SignInActivity) {
                setResult(Activity.RESULT_OK)
                finish()
            }

            // LiveData to notifies user signed in fail
            eventSignInFail.observe(this@SignInActivity) {
                Toast.makeText(this@SignInActivity, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Override to lock onBackPressed event, so this screen is must finished screen
    override fun onBackPressed() {}

    /**
     * Builds a spannable string contains link to Terms of use and privacy policy
     */
    private fun makePrivacyPolicyLink() {
        // Gets strings from resource file
        val termsOfUse = getString(R.string.label_terms_of_use)
        val privacyPolicy = getString(R.string.label_privacy_policy)
        val policySpannableString = SpannableString(getString(R.string.format_terms_of_use_and_policy, termsOfUse, privacyPolicy))

        // Gets start index Terms of use and privacy policy in full spannable string
        val startIndexTOU = policySpannableString.indexOf(termsOfUse, ignoreCase = true)
        val startIndexPolicy = policySpannableString.indexOf(privacyPolicy, ignoreCase = true)

        val clickableSpanTermsOfUse: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openBrowser(Constant.URL_TERM_OF_USE)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                styleLinkColor(ds)
            }
        }

        val clickableSpanPolicy: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openBrowser(Constant.URL_PRIVACY_POLICY)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                styleLinkColor(ds)
            }
        }

        // Sets clickableSpans in order to let user lick to connected links
        policySpannableString.setSpan(clickableSpanTermsOfUse, startIndexTOU, startIndexTOU + termsOfUse.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        policySpannableString.setSpan(clickableSpanPolicy, startIndexPolicy, startIndexPolicy + privacyPolicy.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvTermsOfUse.apply {
            text = policySpannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun openBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun styleLinkColor(ds: TextPaint) {
        ds.color = ContextCompat.getColor(this, R.color.link_color)
        ds.isUnderlineText = false
    }
}