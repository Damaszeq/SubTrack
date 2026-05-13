package pl.lab2.subtrack.ui.components

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
    // Prosta konwersja nazwy na domenę (można to potem rozbudować)
    val domain = serviceName.lowercase().replace(" ", "") + ".com"
    val logoUrl = "https://logo.clearbit.com/$domain"

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(logoUrl)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED) // Wymuszenie zapisu na dysku
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