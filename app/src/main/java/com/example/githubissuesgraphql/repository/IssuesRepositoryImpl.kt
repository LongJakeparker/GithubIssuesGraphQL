package com.example.githubissuesgraphql.repository

import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.toInput
import com.apollographql.apollo.coroutines.await
import com.example.githubissuesgraphql.*
import com.example.githubissuesgraphql.networking.GithubGraphQLApi
import com.example.githubissuesgraphql.room.IssueDao
import com.example.githubissuesgraphql.room.IssueEntity
import javax.inject.Inject

/**
 * @author longtran
 * @since 06/06/2021
 */
class IssuesRepositoryImpl @Inject constructor(
    private val webService: GithubGraphQLApi,
    private val issueDao: IssueDao
) : IssuesRepository {

    companion object {
        const val REPO_OWNER = "apollographql"
        const val REPO_NAME = "apollo"
    }

    /**
     * Queries all issues from repo
     * @return Response<RepositoryIssueQuery.Data>
     */
    override suspend fun queryIssuesList(): Response<RepositoryIssueQuery.Data> {
        return webService.getApolloClient()
            .query(RepositoryIssueQuery(repoOwner = REPO_OWNER, repoName = REPO_NAME)).await()
    }

    /**
     * Queries all issues from repo with end cursor for endless load feature
     * @param endCursor
     * @return Response<RepositoryIssueQuery.Data>
     */
    override suspend fun queryIssuesList(endCursor: String): Response<RepositoryIssueQuery.Data> {
        return webService.getApolloClient().query(
            RepositoryIssueQuery(
                endCursor = endCursor.toInput(),
                repoOwner = REPO_OWNER,
                repoName = REPO_NAME
            )
        ).await()
    }

    /**
     * Queries issue detail
     * @param number
     * @return Response<RepositoryIssueDetailQuery.Data>
     */
    override suspend fun queryIssueDetail(number: Int): Response<RepositoryIssueDetailQuery.Data> {
        return webService.getApolloClient()
            .query(RepositoryIssueDetailQuery(number, repoOwner = REPO_OWNER, repoName = REPO_NAME))
            .await()
    }

    /**
     * Mutation to add new comment to issue
     * @param issueId
     * @param body
     * @return Response<AddCommentToIssueMutation.Data>
     */
    override suspend fun addCommentToIssue(
        issueId: String,
        body: String
    ): Response<AddCommentToIssueMutation.Data> {
        return webService.getApolloClient().mutate(AddCommentToIssueMutation(issueId, body)).await()
    }

    /**
     * Mutation to create a new issue
     * @param repositoryId
     * @param title
     * @param comment
     * @return Response<CreateNewIssueMutation.Data>
     */
    override suspend fun createNewIssue(
        repositoryId: String,
        title: String,
        comment: String?
    ): Response<CreateNewIssueMutation.Data> {
        return webService.getApolloClient().mutate(CreateNewIssueMutation(repositoryId, title, comment.toInput())).await()
    }

    /**
     * Mutation to close a issue
     * @param issueId
     * @return Response<CloseIssueMutation.Data>
     */
    override suspend fun closeIssue(issueId: String): Response<CloseIssueMutation.Data> {
        return webService.getApolloClient().mutate(CloseIssueMutation(issueId)).await()
    }

    /**
     * Mutation to re-open a issue
     * @param issueId
     * @return Response<ReopenIssueMutation.Data>
     */
    override suspend fun reopenIssue(issueId: String): Response<ReopenIssueMutation.Data> {
        return webService.getApolloClient().mutate(ReopenIssueMutation(issueId)).await()
    }

    /**
     * Queries all issues from Room database
     * @return List<IssueEntity>
     */
    override suspend fun getLocalIssues(): List<IssueEntity> {
        return issueDao.getAllIssues()
    }

    /**
     * Inserts all issues into Room database
     * @param issues
     */
    override suspend fun insertIssues(issues: List<IssueEntity?>?) {
        return issueDao.insertAllIssues(issues)
    }

    /**
     * Removes all issues in Room database
     */
    override suspend fun removeIssues() {
        return issueDao.removeAll()
    }
}