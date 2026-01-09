package com.codetech.manga.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codetech.manga.data.model.ChapterData
import com.codetech.manga.data.model.MangaData
import com.codetech.manga.data.repository.MangaRepository
import com.codetech.manga.presentation.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailUiState(
    val mangaState: UiState<MangaData> = UiState.Loading,
    val chaptersState: UiState<List<ChapterData>> = UiState.Loading
) {
    // Convenience properties
    val isLoading: Boolean
        get() = mangaState is UiState.Loading || chaptersState is UiState.Loading

    val hasError: Boolean
        get() = mangaState is UiState.Error || chaptersState is UiState.Error

    val manga: MangaData?
        get() = (mangaState as? UiState.Success)?.data

    val chapters: List<ChapterData>?
        get() = (chaptersState as? UiState.Success)?.data
}

class MangaDetailViewModel(
    private val repository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    // Cache last loaded mangaId to avoid redundant calls
    private var lastLoadedId: String? = null

    fun loadManga(mangaId: String, forceRefresh: Boolean = false) {
        // Skip if already loaded and not forced
        if (mangaId == lastLoadedId && !forceRefresh && _uiState.value.mangaState is UiState.Success) {
            return
        }

        lastLoadedId = mangaId

        viewModelScope.launch {
            // 1. Load manga details
            _uiState.value = _uiState.value.copy(mangaState = UiState.Loading)
            try {
                val manga = repository.getMangaDetails(mangaId)
                    ?: throw Exception("Manga not found (404 or invalid ID)")
                _uiState.value = _uiState.value.copy(mangaState = UiState.Success(manga))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    mangaState = UiState.Error(
                        message = e.message ?: "Failed to load manga details",
                        retry = { loadManga(mangaId, forceRefresh = true) }
                    )
                )
            }

            // 2. Load chapters (only if manga loaded successfully or independently retry)
            _uiState.value = _uiState.value.copy(chaptersState = UiState.Loading)
            try {
                val chapters = repository.getChapters(mangaId)
                _uiState.value = _uiState.value.copy(chaptersState = UiState.Success(chapters))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    chaptersState = UiState.Error(
                        message = e.message ?: "Failed to load chapters",
                        retry = { loadManga(mangaId, forceRefresh = true) }
                    )
                )
            }
        }
    }

    fun retryChapters() {
        val currentId = lastLoadedId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(chaptersState = UiState.Loading)
            try {
                val chapters = repository.getChapters(currentId)
                _uiState.value = _uiState.value.copy(chaptersState = UiState.Success(chapters))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    chaptersState = UiState.Error(
                        message = e.message ?: "Failed to load chapters",
                        retry = ::retryChapters
                    )
                )
            }
        }
    }

    fun refresh() {
        val currentId = lastLoadedId ?: return
        loadManga(currentId, forceRefresh = true)
    }
}