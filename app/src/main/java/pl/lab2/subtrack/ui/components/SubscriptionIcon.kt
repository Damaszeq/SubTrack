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
    var cleanName = serviceName.lowercase().trim()
        .replace(" ", "")
        .replace("+", "")
        .replace("!", "")

    val suffixesToRemove = listOf("premium", "plus", "online", "go", "gold")
    for (suffix in suffixesToRemove) {
        if (cleanName.endsWith(suffix) && cleanName != suffix) {
            cleanName = cleanName.removeSuffix(suffix)
        }
    }

    val googleDomains = mapOf(
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
        "youtube" to "youtube.com",
        "appletv" to "tv.apple.com",
        "empik" to "empik.com",
        "allegrosmart" to "allegro.pl",
        "amazonprime" to "amazon.pl",
        "nintendoswitch" to "nintendo.com",
        "adobecreativecloud" to "adobe.com",
        "chatgpt" to "openai.com",
        "icloud" to "apple.com",
        "linkedin" to "linkedin.com",
        "newsweekpolska" to "newsweek.pl",
        "onet" to "onet.pl",
        "politykacyfrowa" to "polityka.pl",
        "multisport" to "kartamultisport.pl",
        "tinder" to "tinder.com",
        "fitatu" to "fitatu.com",
        "flo" to "flo.health"
    )

    val duckDuckGoDomains = mapOf(
        "glovo" to "glovoapp.com",
        "ubisoft" to "ubisoft.com",
        "xbox" to "xbox.com",
        "ea" to "ea.com",
        "duolingo" to "duolingo.com"
    )

    val logoUrl = when {
        duckDuckGoDomains.containsKey(cleanName) -> {
            val domain = duckDuckGoDomains[cleanName]!!
            "https://icons.duckduckgo.com/ip3/$domain.ico"
        }
        googleDomains.containsKey(cleanName) -> {
            val domain = googleDomains[cleanName]!!
            "https://www.google.com/s2/favicons?domain=$domain&sz=128"
        }
        // Obsługa sytuacji, gdy użytkownik wpisze ręcznie domenę z kropką
        cleanName.contains(".") -> {
        }
        // Dynamiczne wyjście awaryjne
        else -> {
            "https://www.google.com/s2/favicons?domain=$cleanName.com&sz=128"
        }
    }

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