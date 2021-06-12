package com.example.githubissuesgraphql.room

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * @author longtran
 * @since 06/06/2021
 */
@Database(entities = [IssueEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun issueDao(): IssueDao

    companion object {
        const val DB_NAME = "app.db"
    }
}