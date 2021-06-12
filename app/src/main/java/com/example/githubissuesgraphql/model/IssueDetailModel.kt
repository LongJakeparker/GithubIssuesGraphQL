package com.example.githubissuesgraphql.model

/**
 * @author longtran
 * @since 06/06/2021
 */
data class IssueDetailModel(
    var info: IssueInfoModel? = null,
    var comments: List<CommentModel>? = ArrayList()
)
