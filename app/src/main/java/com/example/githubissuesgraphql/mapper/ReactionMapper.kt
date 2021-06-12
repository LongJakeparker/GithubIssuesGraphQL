package com.example.githubissuesgraphql.mapper

import com.example.githubissuesgraphql.AddCommentToIssueMutation
import com.example.githubissuesgraphql.IssueCommentsLoadMoreQuery
import com.example.githubissuesgraphql.RepositoryIssueDetailQuery
import com.example.githubissuesgraphql.util.GeneralUtil
import com.example.githubissuesgraphql.model.ReactionModel

/**
 * @author longtran
 * @since 07/06/2021
 */
object ReactionMapper {
    private fun transform(entity: IssueCommentsLoadMoreQuery.ReactionGroup): ReactionModel {
        return ReactionModel(
            entity.content.rawValue,
            GeneralUtil.getEmoji(entity.content),
            entity.users.totalCount
        )
    }

    fun transformCollection(entityCollection: List<IssueCommentsLoadMoreQuery.ReactionGroup?>?): List<ReactionModel> {
        val result = ArrayList<ReactionModel>()
        if (entityCollection == null)
            return result

        for (entity in entityCollection) {
            entity?.let { result.add(transform(it)) }
        }
        return result
    }

    private fun transform(entity: RepositoryIssueDetailQuery.ReactionGroup): ReactionModel {
        return ReactionModel(
            entity.content.rawValue,
            GeneralUtil.getEmoji(entity.content),
            entity.users.totalCount
        )
    }

    private fun transform(entity: RepositoryIssueDetailQuery.ReactionGroup1): ReactionModel {
        return ReactionModel(
            entity.content.rawValue,
            GeneralUtil.getEmoji(entity.content),
            entity.users.totalCount
        )
    }

    fun transformCollectionDetail(entityCollection: List<RepositoryIssueDetailQuery.ReactionGroup?>?): List<ReactionModel> {
        val result = ArrayList<ReactionModel>()
        if (entityCollection == null)
            return result

        for (entity in entityCollection) {
            entity?.let { result.add(transform(it)) }
        }
        return result
    }

    fun transformCollectionComment(entityCollection: List<RepositoryIssueDetailQuery.ReactionGroup1?>?): List<ReactionModel> {
        val result = ArrayList<ReactionModel>()
        if (entityCollection == null)
            return result

        for (entity in entityCollection) {
            entity?.let { result.add(transform(it)) }
        }
        return result
    }

    private fun transform(entity: AddCommentToIssueMutation.ReactionGroup): ReactionModel {
        return ReactionModel(
            entity.content.rawValue,
            GeneralUtil.getEmoji(entity.content),
            entity.users.totalCount
        )
    }

    fun transformCollectionNewComment(entityCollection: List<AddCommentToIssueMutation.ReactionGroup?>?): List<ReactionModel> {
        val result = ArrayList<ReactionModel>()
        if (entityCollection == null)
            return result

        for (entity in entityCollection) {
            entity?.let { result.add(transform(it)) }
        }
        return result
    }
}