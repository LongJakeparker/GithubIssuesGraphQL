package com.example.githubissuesgraphql.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


/**
 * @author longtran
 * @since 06/06/2021
 */
@Dao
interface IssueDao {
    @Query("SELECT * FROM ${IssueEntity.TABLE_NAME}")
    suspend fun getAllIssues(): List<IssueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllIssues(issues: List<IssueEntity?>?)

    @Query("DELETE FROM ${IssueEntity.TABLE_NAME}")
    suspend fun removeAll()
}