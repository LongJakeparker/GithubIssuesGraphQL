package com.example.githubissuesgraphql.di

import android.content.Context
import androidx.room.Room
import com.example.githubissuesgraphql.SharedPreferencesManager
import com.example.githubissuesgraphql.networking.GithubGraphQLApi
import com.example.githubissuesgraphql.room.AppDatabase
import com.example.githubissuesgraphql.room.IssueDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author longtran
 * @since 06/06/2021
 */
@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            AppDatabase.DB_NAME
        ).build()
    }

    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): IssueDao {
        return appDatabase.issueDao()
    }

    @Singleton
    @Provides
    fun provideWebService(sharedPreferencesManager: SharedPreferencesManager) = GithubGraphQLApi(sharedPreferencesManager)

}