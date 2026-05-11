package com.namma.kathey.ui.home

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
class HomeViewModel @Inject constructor(
    private val repo: HeroRepository,
    val prefs: PreferencesHelper
) : ViewModel() {

    val heroes: StateFlow<List<Hero>> = repo.getAllHeroes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val badgeCount: StateFlow<Int> = repo.getBadgeCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val useKannada: StateFlow<Boolean> = prefs.useKannada
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _districts = MutableStateFlow<List<String>>(emptyList())
    val districts: StateFlow<List<String>> = _districts.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Hero>>(emptyList())
    val searchResults: StateFlow<List<Hero>> = _searchResults.asStateFlow()

    init {
        viewModelScope.launch {
            repo.seedIfNeeded()
            _districts.value = repo.getAllDistricts()
        }
    }

    fun onSearch(q: String) {
        _searchQuery.value = q
        viewModelScope.launch {
            _searchResults.value = if (q.isBlank()) emptyList() else repo.search(q)
        }
    }
}
