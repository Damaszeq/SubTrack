package pl.lab2.subtrack.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit
) {
    val currentTheme by viewModel.themeMode.collectAsState()
    val currentLanguage by viewModel.language.collectAsState()

    val isNotificationsEnabled by viewModel.isNotificationsEnabledGlobal.collectAsState()
    val globalReminderHours by viewModel.globalReminderHours.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.settings_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // ================= SEKCJA 1: WYBÓR MOTYWU =================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(id = R.string.theme_section_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingsOptionCard(
                        label = stringResource(id = R.string.theme_system),
                        icon = Icons.Default.Android,
                        selected = currentTheme == AppThemeMode.SYSTEM,
                        onClick = { viewModel.setThemeMode(AppThemeMode.SYSTEM) },
                        modifier = Modifier.weight(1f)
                    )

                    SettingsOptionCard(
                        label = stringResource(id = R.string.theme_light),
                        icon = Icons.Default.WbSunny,
                        selected = currentTheme == AppThemeMode.LIGHT,
                        onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) },
                        modifier = Modifier.weight(1f)
                    )

                    SettingsOptionCard(
                        label = stringResource(id = R.string.theme_dark),
                        icon = Icons.Default.NightsStay,
                        selected = currentTheme == AppThemeMode.DARK,
                        onClick = { viewModel.setThemeMode(AppThemeMode.DARK) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))

            // ================= SEKCJA 2: WYBÓR JĘZYKA =================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(id = R.string.language_section_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingsOptionCard(
                        label = stringResource(id = R.string.lang_polish),
                        icon = Icons.Default.Translate,
                        selected = currentLanguage == AppLanguage.POLISH,
                        onClick = { viewModel.setLanguage(AppLanguage.POLISH) },
                        modifier = Modifier.weight(1f)
                    )

                    SettingsOptionCard(
                        label = stringResource(id = R.string.lang_english),
                        icon = Icons.Default.Translate,
                        selected = currentLanguage == AppLanguage.ENGLISH,
                        onClick = { viewModel.setLanguage(AppLanguage.ENGLISH) },
                        modifier = Modifier.weight(1f)
                    )
                    SettingsOptionCard(
                        label = stringResource(id = R.string.lang_chinesse),
                        icon = Icons.Default.Translate,
                        selected = currentLanguage == AppLanguage.CHINESE,
                        onClick = { viewModel.setLanguage(AppLanguage.CHINESE) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))

            // ================= SEKCJA 3: POWIADOMIENIA GLOBALNE =================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Domyślne powiadomienia",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingsOptionCard(
                        label = "Wyłączone",
                        icon = Icons.Default.NotificationsOff,
                        selected = !isNotificationsEnabled,
                        onClick = { viewModel.setGlobalNotificationsEnabled(false) },
                        modifier = Modifier.weight(1f)
                    )

                    SettingsOptionCard(
                        label = "Włączone",
                        icon = Icons.Default.NotificationsActive,
                        selected = isNotificationsEnabled,
                        onClick = { viewModel.setGlobalNotificationsEnabled(true) },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }

                // Dynamiczne kafelki szczegółowego czasu pokazywane tylko, gdy powiadomienia są aktywne
                AnimatedVisibility(visible = isNotificationsEnabled) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Kiedy chcesz otrzymać domyślne przypomnienie?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(24, 48, 72).forEach { hour ->
                                val isSelected = globalReminderHours.contains(hour)
                                SettingsOptionCard(
                                    label = if (hour < 24) "$hour godz." else "${hour / 24} dni przed",
                                    icon = Icons.Default.Notifications,
                                    selected = isSelected,
                                    onClick = {
                                        // ZMIANA: Aktualizacja zbioru godzin przypomnień bezpośrednio w ViewModelu
                                        val newHours = if (isSelected) {
                                            if (globalReminderHours.size > 1) globalReminderHours - hour else globalReminderHours
                                        } else {
                                            globalReminderHours + hour
                                        }
                                        viewModel.setGlobalReminderHours(newHours)
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsOptionCard(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strokeColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
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
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}