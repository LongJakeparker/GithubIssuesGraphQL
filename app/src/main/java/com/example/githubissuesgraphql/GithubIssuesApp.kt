package com.example.githubissuesgraphql

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

/**
 * @author longtran
 * @since 06/06/2021
 */
@HiltAndroidApp
class GithubIssuesApp : Application() {
    companion object {
        lateinit var instance: GithubIssuesApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}