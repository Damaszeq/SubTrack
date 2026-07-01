package pl.lab2.subtrack.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.ui.components.FinancePieChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialStatsScreen(
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit
) {
    // Dynamicznie wybieramy źródło danych w zależności od stanu przełącznika
    val subData by viewModel.pieChartData.collectAsState()
    val catData by viewModel.categoryChartData.collectAsState()

    val currentView = viewModel.currentViewType
    val chartData = if (currentView == StatsViewType.BY_SUBSCRIPTION) subData else catData
    val totalMonthlySpending = chartData.sumOf { it.value }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Statystyki",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz"
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // NOWA SEKCJA: Minimalistyczny przełącznik widoku (Premium Segmented Control)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val activeColor = MaterialTheme.colorScheme.background
                    val inactiveColor = Color.Transparent

                    // Przycisk: Subskrypcje
                    Button(
                        onClick = { viewModel.toggleViewType(StatsViewType.BY_SUBSCRIPTION) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentView == StatsViewType.BY_SUBSCRIPTION) activeColor else inactiveColor,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        elevation = if (currentView == StatsViewType.BY_SUBSCRIPTION) ButtonDefaults.buttonElevation(1.dp) else null,
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text("Usługi", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    }

                    // Przycisk: Kategorie
                    Button(
                        onClick = { viewModel.toggleViewType(StatsViewType.BY_CATEGORY) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentView == StatsViewType.BY_CATEGORY) activeColor else inactiveColor,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        elevation = if (currentView == StatsViewType.BY_CATEGORY) ButtonDefaults.buttonElevation(1.dp) else null,
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text("Kategorie", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // SEKCJA 1: Wykres (Automatycznie zaanimuje się na nowo przy przełączeniu danych)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FinancePieChart(
                        data = chartData,
                        totalAmount = totalMonthlySpending,
                        modifier = Modifier.size(240.dp)
                    )
                }
            }

            // SEKCJA 1.5: Nowoczesny Panel Podsumowujący
            item {
                val mostExpensive = remember(chartData) { chartData.maxByOrNull { it.value } }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "PROGNOZA ROCZNA",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = String.format(LocalLocale.current.platformLocale, "%.2f zł", totalMonthlySpending * 12),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (currentView == StatsViewType.BY_SUBSCRIPTION) "AKTYWNE USŁUGI" else "UŻYTE KATEGORIE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${chartData.size}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (mostExpensive != null) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (currentView == StatsViewType.BY_SUBSCRIPTION) "Najwyższy koszt" else "Najdroższa kategoria",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = mostExpensive.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = String.format(LocalLocale.current.platformLocale, "%.2f zł / mc", mostExpensive.value),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // SEKCJA 2: Nagłówek struktury wydatków
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp, start = 4.dp, end = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = if (currentView == StatsViewType.BY_SUBSCRIPTION) "Struktura wydatków" else "Podział na kategorie",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                }
            }

            // SEKCJA 3: Dynamiczna Lista (Zmienia opisy i procenty w locie)
            itemsIndexed(chartData.sortedByDescending { it.value }) { index, entry ->
                val percentage = if (totalMonthlySpending > 0) (entry.value / totalMonthlySpending) * 100 else 0.0

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 6.dp, height = 24.dp)
                                .clip(CircleShape)
                                .background(entry.color)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = String.format(LocalLocale.current.platformLocale, "%.1f%% całkowitego budżetu", percentage),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = String.format(LocalLocale.current.platformLocale, "%.2f zł", entry.value),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    if (index < chartData.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 22.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}