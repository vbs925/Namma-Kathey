package com.namma.kathey.data.dao

import androidx.room.*
import com.namma.kathey.data.model.BadgeRecord
import com.namma.kathey.data.model.Hero
import kotlinx.coroutines.flow.Flow

@Dao
interface HeroDao {
    @Query("SELECT * FROM heroes ORDER BY nameEn ASC")
    fun getAllHeroes(): Flow<List<Hero>>

    @Query("SELECT * FROM heroes WHERE district = :district ORDER BY nameEn ASC")
    fun getHeroesByDistrict(district: String): Flow<List<Hero>>

    @Query("SELECT * FROM heroes WHERE id = :id")
    suspend fun getHeroById(id: Int): Hero?

    @Query("SELECT DISTINCT district FROM heroes ORDER BY district ASC")
    suspend fun getAllDistricts(): List<String>

    @Query("SELECT * FROM heroes WHERE lower(nameEn) LIKE '%'||lower(:q)||'%' OR lower(nameKn) LIKE '%'||lower(:q)||'%'")
    suspend fun search(q: String): List<Hero>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(heroes: List<Hero>)

    @Query("SELECT COUNT(*) FROM heroes")
    suspend fun count(): Int
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges ORDER BY earnedAt DESC")
    fun getAllBadges(): Flow<List<BadgeRecord>>

    @Query("SELECT COUNT(*) FROM badges")
    fun getBadgeCount(): Flow<Int>

    @Query("SELECT COUNT(*) > 0 FROM badges WHERE heroId = :heroId")
    suspend fun hasBadge(heroId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(badge: BadgeRecord)
}
