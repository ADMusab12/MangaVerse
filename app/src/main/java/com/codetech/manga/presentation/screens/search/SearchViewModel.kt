package com.codetech.manga.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codetech.manga.data.model.MangaData
import com.codetech.manga.data.repository.MangaRepository
import com.codetech.manga.presentation.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: MangaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<MangaData>>>(UiState.Success(emptyList()))
    val uiState: StateFlow<UiState<List<MangaData>>> = _uiState.asStateFlow()

    fun search(query: String) {
        if (query.trim().length < 2) {
            _uiState.value = UiState.Success(emptyList())
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val results = repository.searchManga(query.trim())
                _uiState.value = UiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Search failed") { search(query) }
            }
        }
    }
}