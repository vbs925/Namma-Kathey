package com.namma.kathey.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heroes")
data class Hero(
    @PrimaryKey val id: Int,
    val nameEn: String,
    val nameKn: String,
    // District as display strings (used by UI for filtering/display)
    val district: String,       // English district name  e.g. "Mysuru"
    val districtKn: String,     // Kannada district name  e.g. "ಮೈಸೂರು"
    val era: String,            // e.g. "1750–1799"
    val categoryEn: String,
    val categoryKn: String,
    val shortBioEn: String,
    val shortBioKn: String,
    val storyPagesEn: String,   // JSON array of story page strings
    val storyPagesKn: String,   // JSON array of story page strings
    val quizJson: String,       // JSON array of QuizQuestionDto
    val statueLocation: String, // Human-readable location name
    val statueLatitude: Double,
    val statueLongitude: Double,
    val colorHex: String,       // e.g. "#FF6B35"
    val emojiIcon: String       // e.g. "⚔️"
)

@Entity(tableName = "badges")
data class BadgeRecord(
    @PrimaryKey val heroId: Int,
    val earnedAt: Long = System.currentTimeMillis()
)

// DTO used for deserializing quiz JSON stored inside Hero.quizJson
data class QuizQuestionDto(
    val questionEn: String,
    val questionKn: String,
    val options: List<String>,    // options in the active language
    val correctIndex: Int
)
