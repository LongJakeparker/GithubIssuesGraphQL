package com.example.githubissuesgraphql.repository

import com.apollographql.apollo.api.Response
import com.example.githubissuesgraphql.*
import com.example.githubissuesgraphql.room.IssueEntity

/**
 * @author longtran
 * @since 06/06/2021
 */
interface IssuesRepository {

    suspend fun queryIssuesList(): Response<RepositoryIssueQuery.Data>

    suspend fun queryIssuesList(endCursor: String): Response<RepositoryIssueQuery.Data>

    suspend fun queryIssueDetail(number: Int): Response<RepositoryIssueDetailQuery.Data>

    suspend fun addCommentToIssue(issueId: String, body: String): Response<AddCommentToIssueMutation.Data>

    suspend fun createNewIssue(repositoryId: String, title: String, comment: String?): Response<CreateNewIssueMutation.Data>

    suspend fun closeIssue(issueId: String): Response<CloseIssueMutation.Data>

    suspend fun reopenIssue(issueId: String): Response<ReopenIssueMutation.Data>

    suspend fun getLocalIssues(): List<IssueEntity>

    suspend fun insertIssues(issues: List<IssueEntity?>?)

    suspend fun removeIssues()

}