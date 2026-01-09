package com.codetech.manga.presentation.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.codetech.manga.data.model.MangaData
import com.codetech.manga.data.repository.MangaRepository
import com.codetech.manga.presentation.component.MangaCard
import com.codetech.manga.presentation.state.UiState
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(navController: NavController, favorites: SnapshotStateList<MangaData>) {
    val viewModel: SearchViewModel = remember { SearchViewModel(MangaRepository()) }
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search manga") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (query.isNotBlank()) {
                    coroutineScope.launch {
                        viewModel.search(query)
                    }
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Search") }

        Spacer(Modifier.height(16.dp))

        when (val state = uiState) {
            is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    Text("No results found", modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(160.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(state.data) { manga ->
                            MangaCard(
                                manga = manga,
                                onClick = { navController.navigate("manga/${manga.id}") }
                            )
                        }
                    }
                }
            }
            is UiState.Error -> {
                Text(
                    state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                state.retry?.let { retry ->
                    Button(onClick = retry) { Text("Retry") }
                }
            }
        }
    }
}