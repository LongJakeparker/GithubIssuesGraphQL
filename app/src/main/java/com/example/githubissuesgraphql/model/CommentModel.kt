package com.example.githubissuesgraphql.model

import java.io.Serializable

/**
 * @author longtran
 * @since 07/06/2021
 */
data class CommentModel(
    var body: String? = "",
    var authorName: String? = "",
    var authorImage: String?= "",
    var createdAt: String? = "",
    var reactions: List<ReactionModel>? = ArrayList()
) : Serializable