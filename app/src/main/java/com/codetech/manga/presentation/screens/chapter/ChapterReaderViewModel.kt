package com.codetech.manga.presentation.screens.chapter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codetech.manga.data.repository.MangaRepository
import com.codetech.manga.presentation.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChapterReaderViewModel(private val repository: MangaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<String>>> = _uiState.asStateFlow()

    fun loadChapter(chapterId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val pages = repository.getChapterImages(chapterId)
                _uiState.value = UiState.Success(pages.ifEmpty { emptyList() })
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    message = e.message ?: "Cannot load pages",
                    retry = { loadChapter(chapterId) }
                )
            }
        }
    }
}