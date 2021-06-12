package com.example.githubissuesgraphql.model

import com.example.githubissuesgraphql.type.IssueState

/**
 * @author longtran
 * @since 07/06/2021
 */
data class IssueInfoModel(
    var id: String = "",
    var number: Int = 0,
    var title: String? = "",
    var body: CommentModel? = null,
    var state: IssueState = IssueState.UNKNOWN__,
    var commentCount: Int? = 0,
)
