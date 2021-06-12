package com.example.githubissuesgraphql.model

/**
 * @author longtran
 * @since 07/06/2021
 */
data class ReactionModel(
    var content: String? = "",
    var emoji: String? = "",
    var totalCount: Int? = 0
)
