package com.namma.kathey.ui.story

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.kathey.data.model.Hero
import com.namma.kathey.data.repository.HeroRepository
import com.namma.kathey.util.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val repo: HeroRepository,
    val prefs: PreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val heroId: Int = checkNotNull(savedStateHandle["heroId"])

    private val _hero = MutableStateFlow<Hero?>(null)
    val hero: StateFlow<Hero?> = _hero.asStateFlow()

    private val _hasBadge = MutableStateFlow(false)
    val hasBadge: StateFlow<Boolean> = _hasBadge.asStateFlow()

    val useKannada: StateFlow<Boolean> = prefs.useKannada
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            _hero.value = repo.getHeroById(heroId)
            _hasBadge.value = repo.hasBadge(heroId)
        }
    }

    fun goToQuiz(onNavigate: (String) -> Unit) {
        onNavigate("quiz/$heroId")
    }
}
