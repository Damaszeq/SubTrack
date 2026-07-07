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
                title = { Text(text = stringResource(id = R.string.settings_title), fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ================= SEKCJA 1: WYBÓR MOTYWU =================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(id = R.string.theme_section_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // ================= SEKCJA 2: WYBÓR JĘZYKA =================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(id = R.string.language_section_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LanguageOptionCard(
                            label = stringResource(id = R.string.lang_polish),
                            flagEmoji = "🇵🇱",
                            selected = currentLanguage == AppLanguage.POLISH,
                            onClick = { viewModel.setLanguage(AppLanguage.POLISH) },
                            modifier = Modifier.weight(1f)
                        )

                        LanguageOptionCard(
                            label = stringResource(id = R.string.lang_english),
                            flagEmoji = "🇬🇧",
                            selected = currentLanguage == AppLanguage.ENGLISH,
                            onClick = { viewModel.setLanguage(AppLanguage.ENGLISH) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LanguageOptionCard(
                            label = stringResource(id = R.string.lang_chinesse),
                            flagEmoji = "🇨🇳",
                            selected = currentLanguage == AppLanguage.CHINESE,
                            onClick = { viewModel.setLanguage(AppLanguage.CHINESE) },
                            modifier = Modifier.weight(1f)
                        )

                        LanguageOptionCard(
                            label = stringResource(id = R.string.lang_spanish),
                            flagEmoji = "🇪🇸",
                            selected = currentLanguage == AppLanguage.SPANISH,
                            onClick = { viewModel.setLanguage(AppLanguage.SPANISH) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // ================= SEKCJA 3: POWIADOMIENIA GLOBALNE =================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(id = R.string.settings_default_notifications),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SettingsOptionCard(
                        label = stringResource(id = R.string.off),
                        icon = Icons.Default.NotificationsOff,
                        selected = !isNotificationsEnabled,
                        onClick = { viewModel.setGlobalNotificationsEnabled(false) },
                        modifier = Modifier.weight(1f)
                    )

                    SettingsOptionCard(
                        label = stringResource(id = R.string.on),
                        icon = Icons.Default.NotificationsActive,
                        selected = isNotificationsEnabled,
                        onClick = { viewModel.setGlobalNotificationsEnabled(true) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Dynamiczne kafelki szczegółowego czasu pokazywane tylko, gdy powiadomienia są aktywne
                AnimatedVisibility(visible = isNotificationsEnabled) {
                    Column(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.settings_notifications_question),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(24, 48, 72).forEach { hour ->
                                val isSelected = globalReminderHours.contains(hour)
                                SettingsOptionCard(
                                    label = if (hour < 24) {
                                        stringResource(id = R.string.settings_hours_format, hour)
                                    } else {
                                        stringResource(id = R.string.settings_days_format, hour / 24)
                                    },
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
            Spacer(modifier = Modifier.height(16.dp))
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
    val containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceContainerLow
    val strokeColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = containerColor,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = strokeColor
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LanguageOptionCard(
    label: String,
    flagEmoji: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceContainerLow
    val strokeColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = containerColor,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = strokeColor
        )
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = flagEmoji,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}