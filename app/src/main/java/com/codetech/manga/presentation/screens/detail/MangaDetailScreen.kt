package com.codetech.manga.presentation.screens.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.codetech.manga.data.model.MangaData
import com.codetech.manga.data.repository.MangaRepository
import com.codetech.manga.presentation.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailScreen(mangaId: String, navController: NavController, favorites: SnapshotStateList<MangaData>) {
    val viewModel = remember { MangaDetailViewModel(MangaRepository()) }
    val detailState by viewModel.uiState.collectAsState()

    LaunchedEffect(mangaId) {
        viewModel.loadManga(mangaId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = (detailState.mangaState as? UiState.Success)?.data?.attributes?.title?.get("en") ?: "Manga Details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val manga = (detailState.mangaState as? UiState.Success)?.data
                    if (manga != null) {
                        val isFavorite = favorites.any { it.id == manga.id }
                        IconButton(onClick = {
                            if (isFavorite) {
                                favorites.removeAll { it.id == manga.id }
                            } else {
                                favorites.add(manga)
                            }
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        }
    ) { innerPadding ->

        when {
            detailState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            detailState.hasError -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Manga error
                    if (detailState.mangaState is UiState.Error) {
                        val error = detailState.mangaState as UiState.Error
                        Text(
                            text = error.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(16.dp))
                        error.retry?.let {
                            Button(onClick = it) { Text("Retry Manga") }
                        }
                    }

                    // Chapters error (separate)
                    if (detailState.chaptersState is UiState.Error) {
                        Spacer(Modifier.height(24.dp))
                        val error = detailState.chaptersState as UiState.Error
                        Text(
                            text = error.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(16.dp))
                        error.retry?.let {
                            Button(onClick = it) { Text("Retry Chapters") }
                        }
                    }
                }
            }

            else -> {
                val manga = detailState.manga ?: return@Scaffold  // safe guard
                val chapters = detailState.chapters ?: emptyList()

                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Cover + basic info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            AsyncImage(
                                model = "https://uploads.mangadex.org/covers/${manga.id}/${
                                    manga.relationships.find { it.type == "cover_art" }?.attributes?.fileName
                                }.512.jpg",
                                contentDescription = "Cover",
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = manga.attributes.title["en"] ?: manga.attributes.title.values.firstOrNull() ?: "Unknown Title",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    manga.attributes.year?.let {
                                        Text("$it", style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Text(
                                        manga.attributes.status.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = when (manga.attributes.status) {
                                            "ongoing" -> MaterialTheme.colorScheme.primary
                                            "completed" -> MaterialTheme.colorScheme.tertiary
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }

                                Spacer(Modifier.height(12.dp))

                                Text(
                                    text = manga.attributes.description["en"] ?: "No description available",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        HorizontalDivider()
                        Spacer(Modifier.height(16.dp))
                        Text("Chapters", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                    }

                    items(chapters) { chapter ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    "Chapter ${chapter.attributes.chapter ?: "?"} • ${chapter.attributes.title ?: "Untitled"}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            supportingContent = {
                                Text(
                                    chapter.attributes.publishAt.substringBefore("T"),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("chapter/${chapter.id}")
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}