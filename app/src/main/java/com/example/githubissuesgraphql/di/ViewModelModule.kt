package com.example.githubissuesgraphql.di

import com.example.githubissuesgraphql.repository.IssuesRepository
import com.example.githubissuesgraphql.repository.IssuesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * @author longtran
 * @since 06/06/2021
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {

    @Binds
    @ViewModelScoped
    abstract fun bindRepository(repo: IssuesRepositoryImpl): IssuesRepository

}