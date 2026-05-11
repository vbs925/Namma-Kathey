package com.namma.kathey.ui.badge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.kathey.data.model.BadgeRecord
import com.namma.kathey.data.model.Hero
import com.namma.kathey.data.repository.HeroRepository
import com.namma.kathey.util.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BadgeUiItem(val hero: Hero, val badge: BadgeRecord)

@HiltViewModel
class BadgeViewModel @Inject constructor(
    private val repo: HeroRepository,
    val prefs: PreferencesHelper
) : ViewModel() {

    val useKannada: StateFlow<Boolean> = prefs.useKannada
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _badgeItems = MutableStateFlow<List<BadgeUiItem>>(emptyList())
    val badgeItems: StateFlow<List<BadgeUiItem>> = _badgeItems.asStateFlow()

    val totalHeroes: StateFlow<Int> = repo.getAllHeroes()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repo.getAllBadges().collect { badges ->
                val items = badges.mapNotNull { badge ->
                    val hero = repo.getHeroById(badge.heroId)
                    hero?.let { BadgeUiItem(it, badge) }
                }
                _badgeItems.value = items
            }
        }
    }
}
