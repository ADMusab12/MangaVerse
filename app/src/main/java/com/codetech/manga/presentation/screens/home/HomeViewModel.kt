package com.codetech.manga.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codetech.manga.data.model.MangaData
import com.codetech.manga.data.repository.MangaRepository
import com.codetech.manga.presentation.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MangaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<MangaData>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<MangaData>>> = _uiState.asStateFlow()

    init { loadPopular() }

    fun loadPopular() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val list = repository.getPopularManga()
                _uiState.value = UiState.Success(list)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Network error", ::loadPopular)
            }
        }
    }
}