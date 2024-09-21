// ! Bu araç @keyiflerolsun tarafından | @KekikAkademi için yazılmıştır.
// ! https://github.com/Amiqo09/Diziyou-Cloudstream

package com.yusiqo

import android.util.Log
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.annotation.JsonProperty

data class DexItem(
    @JsonProperty("id") val id: String?,
    @JsonProperty("TMDB_ID") val tmdbId: String?,
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String?,
    @JsonProperty("genres") val genres: String?,
    @JsonProperty("release_date") val releaseDate: String?,
    @JsonProperty("runtime") val runtime: String?,
    @JsonProperty("poster") val poster: String,
    @JsonProperty("banner") val banner: String?,
    @JsonProperty("youtube_trailer") val youtubeTrailer: String?,
    @JsonProperty("downloadable") val downloadable: String?,
    @JsonProperty("type") val type: String?,
    @JsonProperty("status") val status: String?,
    @JsonProperty("content_type") val contentType: String?,
    @JsonProperty("custom_tag") val customTag: CustomTag?
)

data class CustomTag(
    @JsonProperty("id") val id: String?,
    @JsonProperty("custom_tags_id") val customTagsId: String?,
    @JsonProperty("content_id") val contentId: String?,
    @JsonProperty("content_type") val contentType: String?,
    @JsonProperty("custom_tags_name") val customTagsName: String?,
    @JsonProperty("background_color") val backgroundColor: String?,
    @JsonProperty("text_color") val textColor: String?
)


data class DexSearch(
    @JsonProperty("channels") val channels:List<DexItem>? = emptyList(),
    @JsonProperty("posters")  val posters:List<DexItem>?  = emptyList(),
)
class DexTv : MainAPI() {
    override var mainUrl              = "https://uydupanel.xyz"
    override var name                 = "DexTV"
    override val hasMainPage          = true
    override var lang                 = "tr"
    override val hasQuickSearch       = false
    override val hasChromecastSupport = true
    override val hasDownloadSupport   = true
    override val supportedTypes       = setOf(TvType.Movie)

    override val mainPage = mainPageOf(
        "${mainUrl}/android/getAllMovies/1"      to "Sayfa 1",
        "${mainUrl}/android/getAllMovies/2"      to "Sayfa 2",
        "${mainUrl}/android/getAllMovies/3"      to "Sayfa 3",
        "${mainUrl}/android/getAllMovies/4"      to "Sayfa 4",
        "${mainUrl}/android/getAllMovies/5"      to "Sayfa 5",
        "${mainUrl}/android/getAllMovies/6"      to "Sayfa 6"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val page = page - 1

        val url  = request.data.replace("SAYFA", "${page}")
        val home = app.get(url)

        val movies = AppUtils.tryParseJson<List<DexItem>>(home.text)!!.mapNotNull { item ->
            val toDict = jacksonObjectMapper().writeValueAsString(item)

            newMovieSearchResponse(item.title, "${toDict}", TvType.Movie) { this.posterUrl = item.image }
        }

        return newHomePageResponse(request.name, movies)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val home    = app.get("${mainUrl}/api/search/${query}/4F5A9C3D9A86FA54EACEDDD635185/c3c5bd17-e37b-4b94-a944-8a3688a30452/")
        val veriler = AppUtils.tryParseJson<DexSearch>(home.text)

        val sonuclar = mutableListOf<SearchResponse>()

        veriler?.channels?.forEach { item ->
            val toDict = jacksonObjectMapper().writeValueAsString(item)

            sonuclar.add(newMovieSearchResponse(item.title, "${toDict}", TvType.Movie) { this.posterUrl = item.image })
        }

        veriler?.posters?.forEach { item ->
            val toDict = jacksonObjectMapper().writeValueAsString(item)

            sonuclar.add(newMovieSearchResponse(item.title, "${toDict}", TvType.Movie) { this.posterUrl = item.image })
        }

        return sonuclar
    }

    override suspend fun quickSearch(query: String): List<SearchResponse> = search(query)

    override suspend fun load(url: String): LoadResponse? {
        val veri = AppUtils.tryParseJson<DexItem>(url) ?: return null

        return newMovieLoadResponse(veri.title, url, TvType.Movie, url) {
            this.posterUrl = veri.image
            this.plot      = veri.description
            this.year      = veri.year
            this.tags      = veri.genres?.map { it.title }
            this.rating    = "${veri.rating}".toRatingInt()
        }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        val veri = AppUtils.tryParseJson<DexItem>(data) ?: return false

        veri.sources.forEach { source ->
            callback.invoke(
                ExtractorLink(
                    source  = this.name,
                    name    = this.name,
                    url     = source.url,
                    referer = "${mainUrl}/",
                    quality = Qualities.Unknown.value,
                    isM3u8  = true
                )
            )
        }

        return true
    }
}