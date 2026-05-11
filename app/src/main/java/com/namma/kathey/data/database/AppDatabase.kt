package com.namma.kathey.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.namma.kathey.data.dao.BadgeDao
import com.namma.kathey.data.dao.HeroDao
import com.namma.kathey.data.model.BadgeRecord
import com.namma.kathey.data.model.Hero

@Database(
    entities = [Hero::class, BadgeRecord::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun heroDao(): HeroDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        const val DB_NAME = "namma_kathey.db"
    }
}
