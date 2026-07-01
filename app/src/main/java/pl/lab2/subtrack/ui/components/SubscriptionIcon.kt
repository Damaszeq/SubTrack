package pl.lab2.subtrack.ui.components

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
    modifier: Modifier = Modifier,
    planKey: String = "" // <-- NOWOŚĆ: Przekazujemy tutaj pole planu/ikony
) {
    val rawIconId = if (planKey.contains("|")) planKey.substringAfter("|") else planKey
    val iconId = rawIconId.lowercase().trim()
    // --- NOWOŚĆ: Obsługa ikon niestandardowych (Custom) ---
    if (iconId.startsWith("custom_")) {
        val iconVector = when (iconId) {
            "custom_star"     -> Icons.Default.Star
            "custom_gym"      -> Icons.Default.FitnessCenter
            "custom_home"     -> Icons.Default.Home
            "custom_code"     -> Icons.Default.Code
            "custom_car"      -> Icons.Default.DirectionsCar
            "custom_school"   -> Icons.Default.School
            "custom_medical"  -> Icons.Default.Favorite
            "custom_shopping" -> Icons.Default.ShoppingCart
            "custom_money"    -> Icons.Default.AccountBalance
            "custom_game"     -> Icons.Default.PlayArrow
            else              -> Icons.Default.Star
        }

        Icon(
            imageVector = iconVector,
            contentDescription = "Custom Icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .size(34.dp)
                .padding(4.dp)
        )
        return // Przerywamy rysowanie, nie ładujemy z sieci!
    }

    // --- Dotychczasowa logika dla presetów sieciowych ---
    val baseName = if (serviceName.contains("(")) {
        serviceName.substringBefore("(").trim()
    } else {
        serviceName
    }

    var cleanName = baseName.lowercase().trim()
        .replace(" ", "")
        .replace("+", "")
        .replace("!", "")
        .replace("-", "")

    val suffixesToRemove = listOf("premium", "plus", "online", "go", "gold", "vod")
    for (suffix in suffixesToRemove) {
        if (cleanName.endsWith(suffix) && cleanName != suffix) {
            cleanName = cleanName.removeSuffix(suffix)
        }
    }

    val googleDomains = mapOf(
        "cda" to "cda.pl",
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
        "empik" to "empik.com",
        "allegrosmart" to "allegro.pl",
        "amazonprime" to "amazon.pl",
        "glovo" to "glovo.com",
        "glovoprime" to "glovo.com",
        "nintendoswitch" to "nintendo.com",
        "ubisoft" to "ubisoft.com",
        "xbox" to "xbox.com",
        "ea" to "ea.com",
        "cdaction" to "cdaction.pl",
        "marvel" to "marvel.com",
        "marvelunlimited" to "marvel.com",
        "googleone" to "one.google.com",
        "google" to "google.com",
        "adobecreativecloud" to "adobe.com",
        "chatgpt" to "openai.com",
        "icloud" to "apple.com",
        "linkedin" to "linkedin.com",
        "carly" to "mycarly.com",
        "wyborcza" to "wyborcza.pl",
        "wyborczapl" to "wyborcza.pl",
        "newsweekpolska" to "newsweek.pl",
        "onet" to "onet.pl",
        "politykacyfrowa" to "polityka.pl",
        "multisport" to "kartamultisport.pl",
        "tinder" to "tinder.com",
        "fitatu" to "fitatu.com",
        "flo" to "flo.health"
    )

    val duckDuckGoDomains = mapOf(
        "duolingo" to "duolingo.com",
        "discord" to "discordapp.com",
        "strava" to "strava.com"
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
        cleanName.contains(".") -> {
            "https://www.google.com/s2/favicons?domain=$cleanName&sz=128"
        }
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