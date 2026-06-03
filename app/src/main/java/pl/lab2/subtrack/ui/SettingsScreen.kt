package pl.lab2.subtrack.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit
) {
    val currentTheme by viewModel.themeMode.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Ustawienia", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Wybierz motyw aplikacji",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Opcja 1: Systemowy
                ThemeOptionCard(
                    label = "System",
                    icon = Icons.Default.Android, // Ikona systemu (możesz też użyć Icons.Default.SettingsSuggest)
                    selected = currentTheme == AppThemeMode.SYSTEM,
                    onClick = { viewModel.setThemeMode(AppThemeMode.SYSTEM) },
                    modifier = Modifier.weight(1f)
                )

                // Opcja 2: Jasny
                ThemeOptionCard(
                    label = "Jasny",
                    icon = Icons.Default.WbSunny, // Ikona słońca
                    selected = currentTheme == AppThemeMode.LIGHT,
                    onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) },
                    modifier = Modifier.weight(1f)
                )

                // Opcja 3: Ciemny
                ThemeOptionCard(
                    label = "Astro",
                    icon = Icons.Default.NightsStay, // Ikona księżyca z gwiazdą
                    selected = currentTheme == AppThemeMode.DARK,
                    onClick = { viewModel.setThemeMode(AppThemeMode.DARK) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
fun ThemeOptionCard(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strokeColor = if (selected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = strokeColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}