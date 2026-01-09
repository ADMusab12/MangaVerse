package com.codetech.manga.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.codetech.manga.data.model.MangaData
import com.codetech.manga.data.repository.MangaRepository
import com.codetech.manga.presentation.component.EmptyState
import com.codetech.manga.presentation.component.ErrorContent
import com.codetech.manga.presentation.component.MangaCard
import com.codetech.manga.presentation.component.ShimmerMangaCard
import com.codetech.manga.presentation.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController,favorites: SnapshotStateList<MangaData>) {
    val viewModel: HomeViewModel = remember { HomeViewModel(MangaRepository()) }
    val uiState by viewModel.uiState.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberPullToRefreshState()

    // Sync refreshing state with UI state
    LaunchedEffect(uiState) {
        isRefreshing = uiState is UiState.Loading
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Popular Manga") },
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { innerPadding ->

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadPopular()
            },
            state = refreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = refreshState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(160.dp),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(12) {
                            ShimmerMangaCard()
                        }
                    }
                }

                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyState("No popular manga found")
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(160.dp),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.data, key = { it.id }) { manga ->
                                MangaCard(
                                    manga = manga,
                                    onClick = { navController.navigate("manga/${manga.id}") }
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = state.retry,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}