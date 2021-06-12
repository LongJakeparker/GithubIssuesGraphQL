package com.example.githubissuesgraphql.mapper

import com.example.githubissuesgraphql.CreateNewIssueMutation
import com.example.githubissuesgraphql.RepositoryIssueQuery
import com.example.githubissuesgraphql.model.IssueInfoModel
import com.example.githubissuesgraphql.model.IssueModel
import com.example.githubissuesgraphql.room.IssueEntity

/**
 * @author longtran
 * @since 06/06/2021
 */
object IssueMapper {
    private fun transform(entity: RepositoryIssueQuery.Node): IssueModel {
        return IssueModel(
            entity.id,
            entity.number,
            entity.title,
            entity.state,
            entity.author?.login,
            entity.author?.avatarUrl.toString(),
            entity.createdAt.toString(),
            entity.comments.totalCount
        )
    }

    fun transformCollection(entityCollection: List<RepositoryIssueQuery.Node?>?): List<IssueModel> {
        val result = ArrayList<IssueModel>()
        if (entityCollection == null)
            return result

        for (entity in entityCollection) {
            entity?.let { result.add(transform(it)) }
        }
        return result
    }

    private fun transformModelToEntity(model: IssueModel): IssueEntity {
        return IssueEntity(
            model.id,
            model.number,
            model.title,
            model.state,
            model.authorName,
            model.authorImage,
            model.createdAt.toString(),
            model.commentCount
        )
    }

    fun transformCollectionModelToEntity(entityCollection: List<IssueModel?>?): List<IssueEntity> {
        val result = ArrayList<IssueEntity>()
        if (entityCollection == null)
            return result

        for (entity in entityCollection) {
            entity?.let { result.add(transformModelToEntity(it)) }
        }
        return result
    }

    private fun transformEntityToModel(entity: IssueEntity): IssueModel {
        return IssueModel(
            entity.id,
            entity.number,
            entity.title,
            entity.state,
            entity.authorName,
            entity.authorImage,
            entity.createdAt.toString(),
            entity.commentCount
        )
    }

    fun transformCollectionEntityToModel(entityCollection: List<IssueEntity?>?): List<IssueModel> {
        val result = ArrayList<IssueModel>()
        if (entityCollection == null)
            return result

        for (entity in entityCollection) {
            entity?.let { result.add(transformEntityToModel(it)) }
        }
        return result
    }

    fun transformNewIssue(entity: CreateNewIssueMutation.Issue): IssueModel {
        return IssueModel(
            entity.id,
            entity.number,
            entity.title,
            entity.state,
            entity.author?.login,
            entity.author?.avatarUrl.toString(),
            entity.createdAt.toString(),
            entity.comments.totalCount
        )
    }

    fun transformIssueInfo(entity: IssueInfoModel): IssueModel {
        return IssueModel(
            entity.id,
            entity.number,
            entity.title,
            entity.state,
            entity.body?.authorName,
            entity.body?.authorImage,
            entity.body?.createdAt,
            entity.commentCount
        )
    }
}