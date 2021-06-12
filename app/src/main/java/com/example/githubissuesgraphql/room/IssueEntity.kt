package com.example.githubissuesgraphql.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.githubissuesgraphql.room.IssueEntity.Companion.TABLE_NAME
import com.example.githubissuesgraphql.type.IssueState

/**
 * @author longtran
 * @since 06/06/2021
 */
@Entity(tableName = TABLE_NAME)
data class IssueEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = "",
    @ColumnInfo(name = "number")
    var number: Int = 0,
    @ColumnInfo(name = "title")
    var title: String? = "",
    @ColumnInfo(name = "state")
    var state: IssueState = IssueState.UNKNOWN__,
    @ColumnInfo(name = "authorName")
    var authorName: String? = "",
    @ColumnInfo(name = "authorImage")
    var authorImage: String? = "",
    @ColumnInfo(name = "createdAt")
    var createdAt: String? = "",
    @ColumnInfo(name = "commentCount")
    var commentCount: Int? = 0
) {
    companion object {
        const val TABLE_NAME = "issue"
    }
}
