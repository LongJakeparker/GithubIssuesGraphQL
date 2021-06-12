package com.example.githubissuesgraphql.mapper

import com.example.githubissuesgraphql.AddCommentToIssueMutation
import com.example.githubissuesgraphql.IssueCommentsLoadMoreQuery
import com.example.githubissuesgraphql.RepositoryIssueDetailQuery
import com.example.githubissuesgraphql.model.CommentModel

/**
 * @author longtran
 * @since 07/06/2021
 */
object CommentMapper {
    private fun transform(entity: IssueCommentsLoadMoreQuery.Node): CommentModel {
        return CommentModel(
            entity.body,
            entity.author?.login,
            entity.author?.avatarUrl.toString(),
            entity.createdAt.toString(),
            ReactionMapper.transformCollection(entity.reactionGroups)
        )
    }

    fun transformCollection(entityCollection: List<IssueCommentsLoadMoreQuery.Node?>?): List<CommentModel> {
        val result = ArrayList<CommentModel>()
        if (entityCollection == null)
            return result

        for (entity in entityCollection) {
            entity?.let { result.add(transform(it)) }
        }
        return result
    }

    private fun transform(entity: RepositoryIssueDetailQuery.Node): CommentModel {
        return CommentModel(
            entity.body,
            entity.author?.login,
            entity.author?.avatarUrl.toString(),
            entity.createdAt.toString(),
            ReactionMapper.transformCollectionComment(entity.reactionGroups)
        )
    }

    fun transformCollectionDetail(entityCollection: List<RepositoryIssueDetailQuery.Node?>?): List<CommentModel> {
        val result = ArrayList<CommentModel>()
        if (entityCollection == null)
            return result

        for (entity in entityCollection) {
            entity?.let { result.add(transform(it)) }
        }
        return result
    }

    fun transform(entity: AddCommentToIssueMutation.Node?): CommentModel {
        return CommentModel(
            entity?.body,
            entity?.author?.login,
            entity?.author?.avatarUrl.toString(),
            entity?.createdAt.toString(),
            ReactionMapper.transformCollectionNewComment(entity?.reactionGroups)
        )
    }
}