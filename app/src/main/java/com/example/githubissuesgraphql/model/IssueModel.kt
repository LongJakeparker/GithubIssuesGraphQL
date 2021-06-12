package com.example.githubissuesgraphql.model

import com.example.githubissuesgraphql.type.IssueState
import java.io.Serializable

/**
 * @author longtran
 * @since 06/06/2021
 */
data class IssueModel(
    var id: String = "",
    var number: Int = 0,
    var title: String? = "",
    var state: IssueState = IssueState.UNKNOWN__,
    var authorName: String? = "",
    var authorImage: String? = "",
    var createdAt: String? = "",
    var commentCount: Int? = 0,
): Serializable
