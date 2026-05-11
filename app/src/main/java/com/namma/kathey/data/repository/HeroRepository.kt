package com.namma.kathey.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.namma.kathey.data.dao.BadgeDao
import com.namma.kathey.data.dao.HeroDao
import com.namma.kathey.data.model.BadgeRecord
import com.namma.kathey.data.model.Hero
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeroRepository @Inject constructor(
    private val heroDao: HeroDao,
    private val badgeDao: BadgeDao,
    private val context: Context
) {
    fun getAllHeroes(): Flow<List<Hero>>            = heroDao.getAllHeroes()
    suspend fun getHeroById(id: Int): Hero?         = heroDao.getHeroById(id)
    suspend fun getAllDistricts(): List<String>      = heroDao.getAllDistricts()
    fun getHeroesByDistrict(district: String): Flow<List<Hero>> = heroDao.getHeroesByDistrict(district)
    suspend fun search(q: String): List<Hero>       = heroDao.search(q)
    fun getAllBadges(): Flow<List<BadgeRecord>>      = badgeDao.getAllBadges()
    fun getBadgeCount(): Flow<Int>                  = badgeDao.getBadgeCount()
    suspend fun hasBadge(heroId: Int): Boolean      = badgeDao.hasBadge(heroId)
    suspend fun awardBadge(heroId: Int)             { badgeDao.insert(BadgeRecord(heroId = heroId)) }

    suspend fun seedIfNeeded() {
        if (heroDao.count() == 0) {
            val json = context.assets.open("heroes.json").bufferedReader().readText()
            val type = object : TypeToken<List<Hero>>() {}.type
            val list: List<Hero> = Gson().fromJson(json, type)
            heroDao.insertAll(list)
        }
    }
}
