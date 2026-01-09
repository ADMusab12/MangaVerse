package com.codetech.manga.data.remote

import com.codetech.manga.data.model.AtHomeResponse
import com.codetech.manga.data.model.ChapterListResponse
import com.codetech.manga.data.model.MangaListResponse
import com.codetech.manga.data.model.MangaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MangaDexApi {
    @GET("manga")
    suspend fun searchManga(
        @Query("title") title: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("includes[]") includes: List<String> = listOf("cover_art")
    ): Response<MangaListResponse>

    @GET("manga/{id}")
    suspend fun getMangaDetails(
        @Path("id") id: String,
        @Query("includes[]") includes: List<String> = listOf("cover_art", "author")
    ): Response<MangaResponse>

    @GET("chapter")
    suspend fun getChapters(
        @Query("manga") mangaId: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("translatedLanguage[]") lang: List<String> = listOf("en"),
        @Query("order[chapter]") order: String = "desc"
    ): Response<ChapterListResponse>

    @GET("at-home/server/{chapterId}")
    suspend fun getChapterImages(@Path("chapterId") chapterId: String): Response<AtHomeResponse>
}