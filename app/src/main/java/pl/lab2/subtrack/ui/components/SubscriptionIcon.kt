package pl.lab2.subtrack.ui.components

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
    // 1. SŁOWNIK WYJĄTKÓW - Tutaj wpisujesz usługi, które po wyczyszczeniu spacji dają złe domeny
    val specialDomains = mapOf(
        "disneyplus" to "disneyplus.com",
        "disney+" to "disneyplus.com",
        "disney" to "disneyplus.com",
        "hbomax" to "max.com",
        "max" to "max.com",
        "allegrosmart" to "allegro.pl",
        "allegrosmart!" to "allegro.pl",
        "allegro" to "allegro.pl",
        "canal+online" to "canalplus.com",
        "canal+" to "canalplus.com",
        "polsatboxgo" to "polsatboxgo.pl",
        "tvpvod" to "tvp.pl",
        "primevideo" to "primevideo.com",
        "amazonprime" to "primevideo.com"
    )

    val cleanName = serviceName.lowercase().trim().replace(" ", "")

    val domain = when {

        specialDomains.containsKey(cleanName) -> specialDomains[cleanName]!!

        // Jeśli użytkownik sam wpisał kropkę (np. "google.pl", "onet.pl"), bierzemy to co wpisał
        cleanName.contains(".") -> cleanName

        // W każdym innym przypadku domyślnie dodajemy .com
        else -> "$cleanName.com"
    }

    val logoUrl = "https://www.google.com/s2/favicons?domain=$domain&sz=128"

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(logoUrl)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .listener(
                onStart = { Log.d("SubTrackIcon", "Próba pobrania: $logoUrl") },
                onSuccess = { _, _ -> Log.d("SubTrackIcon", "Sukces! Pobrano logo dla: $domain") },
                onError = { _, result ->
                    Log.e("SubTrackIcon", "Błąd dla $domain. Komunikat: ${result.throwable.message}")
                    result.throwable.printStackTrace()
                }
            )
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

@Preview(showBackground = true)
@Composable
fun SubscriptionIconPreview() {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {

        SubscriptionIcon(serviceName = "Netflix")
        SubscriptionIcon(serviceName = "disney+")
        SubscriptionIcon(serviceName = "Disney Plus")
        SubscriptionIcon(serviceName = "YouTube")
        SubscriptionIcon(serviceName = "allegro")
        SubscriptionIcon(serviceName = "wp.pl")
        SubscriptionIcon(serviceName = "JakasNieistniejacaUsługa123")
    }
}