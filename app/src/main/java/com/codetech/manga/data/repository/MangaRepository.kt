package com.codetech.manga.data.repository

import com.codetech.manga.data.RetrofitClient
import com.codetech.manga.data.model.ChapterData
import com.codetech.manga.data.model.MangaData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MangaRepository {
    private val api = RetrofitClient.api

    suspend fun searchManga(query: String): List<MangaData> = withContext(Dispatchers.IO) {
        val response = api.searchManga(title = query)
        if (response.isSuccessful) response.body()?.data ?: emptyList() else emptyList()
    }

    suspend fun getPopularManga(): List<MangaData> = withContext(Dispatchers.IO) {
        val response = api.searchManga(limit = 20)
        if (response.isSuccessful) response.body()?.data ?: emptyList() else emptyList()
    }

    suspend fun getMangaDetails(mangaId: String): MangaData? = withContext(Dispatchers.IO) {
        val response = api.getMangaDetails(mangaId)
        if (response.isSuccessful) response.body()?.data else null
    }

    suspend fun getChapters(mangaId: String): List<ChapterData> = withContext(Dispatchers.IO) {
        val response = api.getChapters(mangaId)
        if (response.isSuccessful) response.body()?.data ?: emptyList() else emptyList()
    }

    suspend fun getChapterImages(chapterId: String): List<String> = withContext(Dispatchers.IO) {
        val response = api.getChapterImages(chapterId)
        if (response.isSuccessful) {
            response.body()?.let { atHome ->
                atHome.chapter.data.map { file ->
                    "${atHome.baseUrl}/data/${atHome.chapter.hash}/$file"
                }
            } ?: emptyList()
        } else emptyList()
    }
}