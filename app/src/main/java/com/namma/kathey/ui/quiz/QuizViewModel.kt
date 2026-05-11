package com.namma.kathey.ui.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.namma.kathey.data.model.Hero
import com.namma.kathey.data.model.QuizQuestionDto
import com.namma.kathey.data.repository.HeroRepository
import com.namma.kathey.util.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizState(
    val hero: Hero? = null,
    val questions: List<QuizQuestionDto> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: Int? = null,
    val answered: Boolean = false,
    val score: Int = 0,
    val finished: Boolean = false,
    val badgeAwarded: Boolean = false
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repo: HeroRepository,
    val prefs: PreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val heroId: Int = checkNotNull(savedStateHandle["heroId"])

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state.asStateFlow()

    val useKannada: StateFlow<Boolean> = prefs.useKannada
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            val hero = repo.getHeroById(heroId)
            val questions: List<QuizQuestionDto> = try {
                val type = object : TypeToken<List<QuizQuestionDto>>() {}.type
                Gson().fromJson(hero?.quizJson ?: "[]", type)
            } catch (e: Exception) { emptyList() }
            _state.value = QuizState(hero = hero, questions = questions)
        }
    }

    fun selectOption(idx: Int) {
        val s = _state.value
        if (s.answered) return
        val correct = s.questions.getOrNull(s.currentIndex)?.correctIndex == idx
        _state.value = s.copy(selectedOption = idx, answered = true, score = if (correct) s.score + 1 else s.score)
    }

    fun next() {
        val s = _state.value
        val nextIdx = s.currentIndex + 1
        if (nextIdx >= s.questions.size) {
            val passed = s.score == s.questions.size
            _state.value = s.copy(finished = true)
            if (passed) viewModelScope.launch {
                repo.awardBadge(heroId)
                _state.value = _state.value.copy(badgeAwarded = true)
            }
        } else {
            _state.value = s.copy(currentIndex = nextIdx, selectedOption = null, answered = false)
        }
    }
}
