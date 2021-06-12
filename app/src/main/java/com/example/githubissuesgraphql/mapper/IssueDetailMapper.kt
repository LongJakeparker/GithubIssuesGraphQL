package com.example.githubissuesgraphql.mapper

import com.example.githubissuesgraphql.RepositoryIssueDetailQuery
import com.example.githubissuesgraphql.model.CommentModel
import com.example.githubissuesgraphql.model.IssueDetailModel
import com.example.githubissuesgraphql.model.IssueInfoModel

/**
 * @author longtran
 * @since 07/06/2021
 */
object IssueDetailMapper {
    fun transform(entity: RepositoryIssueDetailQuery.Issue?): IssueDetailModel {
        return if (entity == null) {
            IssueDetailModel()
        } else {
            IssueDetailModel(
                IssueInfoModel(
                    entity.id,
                    entity.number,
                    entity.title,
                    CommentModel(
                        entity.body,
                        entity.author?.login,
                        entity.author?.avatarUrl.toString(),
                        entity.createdAt.toString(),
                        ReactionMapper.transformCollectionDetail(entity.reactionGroups)
                    ),
                    entity.state,
                    entity.comments.totalCount
                ),
                CommentMapper.transformCollectionDetail(entity.comments.nodes)
            )
        }
    }

    fun transformCollection(entityCollection: List<RepositoryIssueDetailQuery.Issue?>?): List<IssueDetailModel> {
        val result = ArrayList<IssueDetailModel>()
        if (entityCollection == null)
            return result

        for (entity in entityCollection) {
            entity?.let { result.add(transform(it)) }
        }
        return result
    }
}