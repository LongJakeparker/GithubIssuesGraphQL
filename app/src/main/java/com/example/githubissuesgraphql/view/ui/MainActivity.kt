package com.example.githubissuesgraphql.view.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.githubissuesgraphql.R
import com.example.githubissuesgraphql.SharedPreferencesManager
import com.example.githubissuesgraphql.custom.setAvatarUrl
import com.example.githubissuesgraphql.databinding.ActivityMainBinding
import com.example.githubissuesgraphql.repository.IssuesRepositoryImpl.Companion.REPO_NAME
import com.example.githubissuesgraphql.repository.IssuesRepositoryImpl.Companion.REPO_OWNER
import com.example.githubissuesgraphql.util.GeneralUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    // Result launcher to get result from sign in screen
    private var signInResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Gets current fragment and call fetch data function to start get data from server
                val navFragment =
                    supportFragmentManager.findFragmentById(binding.navHostFragment.id)
                val childFragment = navFragment!!.childFragmentManager.primaryNavigationFragment
                (childFragment as BaseBindingFragment<*>).fetchData()

                // Binds signed in user's information to toolbar
                bindUserData()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Create a custom toolbar
        setUpCustomToolBar()
        // Setup navigate controller
        setUpNavController()

        // If user is not signed in then open sign in screen, else bind user's information
        if (sharedPreferencesManager.getAccessToken().isEmpty()) {
            SignInActivity.startForResult(this, signInResultLauncher)
        } else {
            bindUserData()
        }
    }

    /**
     * Setups navigate controller
     */
    private fun setUpNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.findNavController()
        setupActionBarWithNavController(navController)
    }

    /**
     * Shows menu popup with sign out option
     * @param view
     */
    private fun showMenuPopup(view: View) {
        val popup = PopupMenu(this@MainActivity, view)
        popup.menuInflater
            .inflate(R.menu.user_popup_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            GeneralUtil.showOptionMessage(
                this,
                R.string.label_sign_out,
                R.string.content_sign_out
            ) { _, _ ->
                onSignOut()
            }
            true
        }

        popup.show()
    }

    /**
     * Signs out current user
     * Things to do:
     *  - Sign out from Firebase
     *  - Remove all local data are associated with user
     *  - Open sign in screen
     */
    private fun onSignOut() {
        Firebase.auth.signOut()
        sharedPreferencesManager.removeSignInData()
        SignInActivity.startForResult(this, signInResultLauncher)
    }

    /**
     * Binds signed in user's information to view
     */
    private fun bindUserData() {
        binding.apply {
            setAvatarUrl(ivAvatar, sharedPreferencesManager.getUser()?.avatarUrl)
            ivAvatar.setOnClickListener {
                showMenuPopup(it)
            }
        }
    }

    /**
     * Setups custom toolbar
     */
    private fun setUpCustomToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.tvRepoInfo.text = getString(R.string.format_repo_info, REPO_OWNER, REPO_NAME)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }

    fun setCustomTitle(title: String?) {
        binding.tvTitle.text = title
    }
}