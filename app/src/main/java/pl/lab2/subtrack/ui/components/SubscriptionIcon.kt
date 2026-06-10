package pl.lab2.subtrack.ui.components

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import pl.lab2.subtrack.R

@Composable
fun SubscriptionIcon(
    serviceName: String,
    modifier: Modifier = Modifier
) {
    // 1. Standaryzacja nazwy wpisanej przez użytkownika
    var cleanName = serviceName.lowercase().trim()
        .replace(" ", "")
        .replace("+", "")
        .replace("!", "")

    // 2. Usuwanie popularnych sufiksów (kolejność ma znaczenie)
    val suffixesToRemove = listOf("premium", "plus", "online", "go", "gold", "vod")
    for (suffix in suffixesToRemove) {
        if (cleanName.endsWith(suffix) && cleanName != suffix) {
            cleanName = cleanName.removeSuffix(suffix)
        }
    }

    // 3. Mapa domen obsługiwanych przez Google Favicon API
    val googleDomains = mapOf(
        // Streaming & VOD
        "disney" to "disneyplus.com",
        "hbomax" to "max.com",
        "max" to "max.com",
        "sky" to "skyshowtime.com",
        "skyshowtime" to "skyshowtime.com",
        "amazonprimevideo" to "primevideo.com",
        "primevideo" to "primevideo.com",
        "player" to "player.pl",
        "polsatbox" to "polsatboxgo.pl",
        "canal" to "canalplus.com",
        "tvp" to "tvp.pl",
        "tvpvod" to "tvp.pl",
        "youtube" to "youtube.com",
        "appletv" to "tv.apple.com",

        // Zakupy & Dostawy
        "empik" to "empik.com",
        "allegrosmart" to "allegro.pl",
        "amazonprime" to "amazon.pl",
        "glovo" to "glovo.com",
        "glovoprime" to "glovo.com",

        // Gaming
        "nintendoswitch" to "nintendo.com",
        "ubisoft" to "ubisoft.com",
        "xbox" to "xbox.com",
        "ea" to "ea.com",

        // Kultura & Produktywność
        "marvel" to "marvel.com",
        "marvelunlimited" to "marvel.com",
        "googleone" to "one.google.com",
        "google" to "google.com",
        "adobecreativecloud" to "adobe.com",
        "chatgpt" to "openai.com",
        "icloud" to "apple.com",
        "linkedin" to "linkedin.com",

        // Prasa & Informacje
        "wyborcza" to "wyborcza.pl",
        "wyborczapl" to "wyborcza.pl",
        "newsweekpolska" to "newsweek.pl",
        "onet" to "onet.pl",
        "politykacyfrowa" to "polityka.pl",

        // Sport, Zdrowie & Inne
        "multisport" to "kartamultisport.pl",
        "tinder" to "tinder.com",
        "fitatu" to "fitatu.com",
        "flo" to "flo.health"
    )

    // 4. Alternatywna mapa dla DuckDuckGo
    val duckDuckGoDomains = mapOf(
        "duolingo" to "duolingo.com"
    )

    // 5. Wyznaczenie poprawnego adresu URL ikony
    val logoUrl = when {
        duckDuckGoDomains.containsKey(cleanName) -> {
            val domain = duckDuckGoDomains[cleanName]!!
            "https://icons.duckduckgo.com/ip3/$domain.ico"
        }
        googleDomains.containsKey(cleanName) -> {
            val domain = googleDomains[cleanName]!!
            "https://www.google.com/s2/favicons?domain=$domain&sz=128"
        }

        cleanName.contains(".") -> {
            "https://www.google.com/s2/favicons?domain=$cleanName&sz=128"
        }
        else -> {
            "https://www.google.com/s2/favicons?domain=$cleanName.com&sz=128"
        }
    }

    // Logowanie ułatwiające debugowanie w Logcat
    Log.d("SubscriptionIcon", "Service: $serviceName -> Cleaned: $cleanName -> URL: $logoUrl")

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(logoUrl)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = "Logo $serviceName",
        placeholder = painterResource(R.drawable.ic_placeholder_sub),
        error = painterResource(R.drawable.ic_placeholder_sub),
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Fit
    )
}