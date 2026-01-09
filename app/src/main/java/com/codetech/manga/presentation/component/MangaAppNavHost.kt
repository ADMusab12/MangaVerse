package com.codetech.manga.presentation.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.codetech.manga.data.model.MangaData
import com.codetech.manga.presentation.screens.chapter.ChapterReaderScreen
import com.codetech.manga.presentation.screens.detail.MangaDetailScreen
import com.codetech.manga.presentation.screens.home.HomeScreen
import com.codetech.manga.presentation.screens.library.LibraryScreen
import com.codetech.manga.presentation.screens.search.SearchScreen

@Composable
fun MangaAppNavHost(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val favorites = remember { mutableStateListOf<MangaData>() }

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf("home", "search", "library")) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") { popUpTo(navController.graph.startDestinationId) } },
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "search",
                        onClick = { navController.navigate("search") },
                        icon = { Icon(Icons.Default.Search, null) },
                        label = { Text("Search") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "library",
                        onClick = { navController.navigate("library") },
                        icon = { Icon(Icons.Default.Bookmarks, null) },
                        label = { Text("Library") })
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") { HomeScreen(navController, favorites) }
            composable("search") { SearchScreen(navController, favorites) }
            composable(
                "manga/{mangaId}",
                arguments = listOf(navArgument("mangaId") { type = NavType.StringType })
            ) { backStackEntry ->
                MangaDetailScreen(
                    mangaId = backStackEntry.arguments?.getString("mangaId") ?: "",
                    navController = navController,
                    favorites = favorites
                )
            }
            composable(
                "chapter/{chapterId}",
                arguments = listOf(navArgument("chapterId") { type = NavType.StringType })
            ) { backStackEntry ->
                ChapterReaderScreen(
                    chapterId = backStackEntry.arguments?.getString("chapterId") ?: "",
                    navController = navController
                )
            }
            composable("library") { LibraryScreen(navController, favorites) }
        }
    }
}
