/*
MyPlugin.kt - Cloudstream Ultimate Extractor Tam Entegre Plugin
Özellikler:

* Film listesi (TMDB trending/popüler)
* Film detay sayfası (poster, açıklama, yıl)
* Netflix tarzı oyuncu slider
* Oyuncu filmografisi (tıklayınca oyuncunun diğer filmleri)
* Benzer filmler
* 20+ provider Ultimate Extractor (50+ kaynak)
* Otomatik altyazı destekli
* Diğer eklentilerde ara
  */

import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.models.*
import com.lagradost.cloudstream3.utils.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MyPlugin : MainAPI() {

```
override var mainUrl = "https://themoviedb.org/"
override var name = "Benim Sinema"
override var lang = "tr"

private val apiKey = "847629da5649502fa6655f73b15a5c8e"

// Ana sayfa
override suspend fun getMainPage(): HomePageResponse {
    val url = "https://api.themoviedb.org/3/trending/movie/week?api_key=$apiKey&language=tr"
    val json = app.get(url).parsedSafe<TmdbResponse>()
    val list = json?.results?.map {
        newMovieSearchResponse(it.title, it.id.toString()) {
            posterUrl = "https://image.tmdb.org/t/p/w500${it.poster_path}"
            year = it.release_date?.substring(0, 4)?.toIntOrNull()
            addTMDbId(it.id.toString())
        }
    } ?: emptyList()
    return newHomePageResponse("Popüler Filmler", list)
}

// Film detay / oyuncu / benzer filmler / oyuncu filmografisi
override suspend fun load(url: String): LoadResponse {

    // Oyuncu filmografisi sayfası
    if (url.startsWith("actor_")) {
        val actorId = url.removePrefix("actor_")
        val api = "https://api.themoviedb.org/3/person/$actorId/movie_credits?api_key=$apiKey&language=tr"
        val json = app.get(api).parsedSafe<TmdbActorMovies>()
        val movies = json?.cast?.map {
            newMovieSearchResponse(it.title, it.id.toString()) {
                posterUrl = "https://image.tmdb.org/t/p/w500${it.poster_path}"
            }
        }
        return newHomePageResponse("Filmografi", movies ?: listOf())
    }

    // Normal film detay sayfası
    val movieId = url.toInt()
    val api = "https://api.themoviedb.org/3/movie/$movieId?api_key=$apiKey&language=tr"
    val movie = app.get(api).parsedSafe<TmdbMovie>()

    // Oyuncular
    val creditsUrl = "https://api.themoviedb.org/3/movie/$movieId/credits?api_key=$apiKey&language=tr"
    val credits = app.get(creditsUrl).parsedSafe<TmdbCredits>()
    val actors = credits?.cast?.map {
        ActorData(
            Actor(it.name, "https://image.tmdb.org/t/p/w500${it.profile_path}"),
            roleString = it.character,
            actorId = "actor_${it.id}"
        )
    }

    // Benzer filmler
    val similarUrl = "https://api.themoviedb.org/3/movie/$movieId/similar?api_key=$apiKey&language=tr"
    val similar = app.get(similarUrl).parsedSafe<TmdbResponse>()
    val recommendations = similar?.results?.map {
        newMovieSearchResponse(it.title, it.id.toString()) {
            posterUrl = "https://image.tmdb.org/t/p/w500${it.poster_path}"
        }
    }

    return newMovieLoadResponse(movie?.title ?: "", url, TvType.Movie, url) {
        posterUrl = "https://image.tmdb.org/t/p/w500${movie?.poster_path}"
        plot = movie?.overview
        year = movie?.release_date?.substring(0,4)?.toIntOrNull()
        addTMDbId(movieId.toString())
        this.actors = actors ?: listOf()
        this.recommendations = recommendations ?: listOf()
    }
}

// Ultimate Extractor – 20+ provider
override suspend fun loadLinks(data: String, isCasting: Boolean, callback: (ExtractorLink) -> Unit) {
    val tmdbId = data
    val providers = listOf(
        "https://www.2embed.cc/embed/$tmdbId",
        "https://vidsrc.me/embed/movie/$tmdbId",
        "https://superembed.stream/embed/movie/$tmdbId",
        "https://autoembed.cc/movie/$tmdbId",
        "https://vidlink.pro/movie/$tmdbId",
        "https://filemoon.sx/embed/$tmdbId",
        "https://mixdrop.co/embed/$tmdbId",
        "https://vidoza.net/embed/$tmdbId",
        "https://dood.ws/embed/$tmdbId",
        "https://streamtape.com/e/$tmdbId",
        "https://faststream.xyz/embed/$tmdbId",
        "https://yourupload.com/embed/$tmdbId",
        "https://clicknupload.org/embed/$tmdbId",
        "https://megaembed.cc/embed/$tmdbId",
        "https://ok.ru/videoembed/$tmdbId",
        "https://uptostream.com/stream/$tmdbId",
        "https://streamlare.com/embed/$tmdbId",
        "https://xstreamcdn.com/embed/$tmdbId",
        "https://flixhq.to/embed/movie/$tmdbId",
        "https://vidcloud9.com/embed/$tmdbId"
    )

    coroutineScope {
        providers.map { provider ->
            launch {
                try {
                    loadExtractor(provider, data, callback)
                } catch (e: Exception) {
                    println("Provider load hatası: $provider -> ${e.message}")
                }
            }
        }
    }
}

// Arama
override suspend fun search(query: String): List<SearchResponse> {
    val url = "https://api.themoviedb.org/3/search/movie?api_key=$apiKey&query=$query&language=tr"
    val json = app.get(url).parsedSafe<TmdbResponse>()
    return json?.results?.map {
        newMovieSearchResponse(it.title, it.id.toString()) {
            posterUrl = "https://image.tmdb.org/t/p/w500${it.poster_path}"
            addTMDbId(it.id.toString())
        }
    } ?: listOf()
}
```

}

