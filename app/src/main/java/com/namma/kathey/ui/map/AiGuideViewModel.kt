package com.namma.kathey.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.kathey.util.GeminiHelper
import com.namma.kathey.util.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiMessage(val text: String, val isUser: Boolean)

@HiltViewModel
class AiGuideViewModel @Inject constructor(
    private val gemini: GeminiHelper,
    val prefs: PreferencesHelper
) : ViewModel() {

    private val _messages = MutableStateFlow<List<AiMessage>>(emptyList())
    val messages: StateFlow<List<AiMessage>> = _messages.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    val useKannada: StateFlow<Boolean> = prefs.useKannada
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun send(text: String) {
        if (text.isBlank() || _loading.value) return
        _messages.value = _messages.value + AiMessage(text, isUser = true)
        _loading.value = true
        viewModelScope.launch {
            val reply = gemini.ask(text)
            _messages.value = _messages.value + AiMessage(reply, isUser = false)
            _loading.value = false
        }
    }

    fun clear() { _messages.value = emptyList() }
}
