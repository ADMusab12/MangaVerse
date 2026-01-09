package com.codetech.manga.data.model

import com.google.gson.annotations.SerializedName

data class MangaListResponse(val data: List<MangaData>)
data class MangaResponse(val data: MangaData)
data class ChapterListResponse(val data: List<ChapterData>)

data class MangaData(
    val id: String,
    val attributes: MangaAttributes,
    val relationships: List<Relationship>
)

data class MangaAttributes(
    val title: Map<String, String>,
    val description: Map<String, String>,
    val status: String,
    val year: Int?
)

data class Relationship(
    val id: String,
    val type: String,
    val attributes: RelationshipAttributes? = null
)

data class RelationshipAttributes(
    @SerializedName("fileName") val fileName: String? = null,
    val name: String? = null
)

data class ChapterData(
    val id: String,
    val attributes: ChapterAttributes
)

data class ChapterAttributes(
    val chapter: String?,
    val title: String?,
    val translatedLanguage: String,
    val publishAt: String
)

data class AtHomeResponse(
    val baseUrl: String,
    val chapter: ChapterImageData
)

data class ChapterImageData(
    val hash: String,
    val data: List<String>,
    val dataSaver: List<String>
)