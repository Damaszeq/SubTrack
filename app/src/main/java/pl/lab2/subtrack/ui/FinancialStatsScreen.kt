package pl.lab2.subtrack.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R
import pl.lab2.subtrack.ui.components.FinancePieChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialStatsScreen(
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit
) {
    // ------------------------------------------------------------------------
    // STAN I DANE
    // ------------------------------------------------------------------------
    val subData by viewModel.pieChartData.collectAsState()
    val catData by viewModel.categoryChartData.collectAsState()
    val timeData by viewModel.timeChartData.collectAsState()
    val currentPeriod by viewModel.selectedPeriod.collectAsState()

    val currentView = viewModel.currentViewType
    val chartData = if (currentView == StatsViewType.BY_SUBSCRIPTION) subData else catData
    val totalMonthlySpending = chartData.sumOf { it.value }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.stats_title),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
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
            // ------------------------------------------------------------------------
            // SEKCJA 1: WYKRES CZASOWY I FILTRY
            // ------------------------------------------------------------------------
            item {
                Text(
                    text = stringResource(id = R.string.stats_history),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, start = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SubscriptionViewModel.TimePeriod.values().forEach { period ->
                        FilterChip(
                            selected = currentPeriod == period,
                            onClick = { viewModel.changeTimePeriod(period) },
                            label = {
                                Text(
                                    text = period.label,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                FinanceTimeChart(
                    data = timeData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
            }

            // ------------------------------------------------------------------------
            // SEKCJA 2: SELEKTOR WIDOKU (SUBSKRYPCJE / KATEGORIE)
            // ------------------------------------------------------------------------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val activeBg = MaterialTheme.colorScheme.background
                    val activeFg = MaterialTheme.colorScheme.onSurface
                    val inactiveFg = MaterialTheme.colorScheme.onSurfaceVariant

                    Button(
                        onClick = { viewModel.toggleViewType(StatsViewType.BY_SUBSCRIPTION) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentView == StatsViewType.BY_SUBSCRIPTION) activeBg else Color.Transparent,
                            contentColor = if (currentView == StatsViewType.BY_SUBSCRIPTION) activeFg else inactiveFg
                        ),
                        contentPadding = PaddingValues(vertical = 10.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(stringResource(id = R.string.stats_tab_subscriptions), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { viewModel.toggleViewType(StatsViewType.BY_CATEGORY) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentView == StatsViewType.BY_CATEGORY) activeBg else Color.Transparent,
                            contentColor = if (currentView == StatsViewType.BY_CATEGORY) activeFg else inactiveFg
                        ),
                        contentPadding = PaddingValues(vertical = 10.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(stringResource(id = R.string.stats_tab_categories), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ------------------------------------------------------------------------
            // SEKCJA 3: WYKRES KOŁOWY (PIE CHART)
            // ------------------------------------------------------------------------
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

            // ------------------------------------------------------------------------
            // SEKCJA 4: PODSUMOWANIE I PROGNOZY (KARTA)
            // ------------------------------------------------------------------------
            item {
                val mostExpensive = remember(chartData) { chartData.maxByOrNull { it.value } }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 28.dp)
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
                                text = stringResource(id = R.string.stats_yearly_forecast),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = String.format(LocalLocale.current.platformLocale, stringResource(id = R.string.currency_format_pln), totalMonthlySpending * 12),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (currentView == StatsViewType.BY_SUBSCRIPTION) stringResource(id = R.string.stats_active_subs) else stringResource(id = R.string.stats_used_cats),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${chartData.size}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (mostExpensive != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (currentView == StatsViewType.BY_SUBSCRIPTION) stringResource(id = R.string.stats_highest_cost) else stringResource(id = R.string.stats_most_expensive_cat),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = mostExpensive.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = String.format(LocalLocale.current.platformLocale, stringResource(id = R.string.currency_per_month_format_pln), mostExpensive.value),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // ------------------------------------------------------------------------
            // SEKCJA 5: NAGŁÓWEK STRUKTURY LISTY
            // ------------------------------------------------------------------------
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (currentView == StatsViewType.BY_SUBSCRIPTION) stringResource(id = R.string.stats_subs_structure) else stringResource(id = R.string.stats_cat_structure),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(modifier = Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                }
            }

            // ------------------------------------------------------------------------
            // SEKCJA 6: ELEMENTY STRUKTURY (LISTA ELEMENTÓW)
            // ------------------------------------------------------------------------
            itemsIndexed(chartData.sortedByDescending { it.value }) { index, entry ->
                val percentage = if (totalMonthlySpending > 0) (entry.value / totalMonthlySpending) * 100 else 0.0
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(width = 6.dp, height = 24.dp).clip(CircleShape).background(entry.color))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = entry.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = String.format(LocalLocale.current.platformLocale, stringResource(id = R.string.stats_percent_format), percentage),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = String.format(LocalLocale.current.platformLocale, stringResource(id = R.string.currency_format_pln), entry.value),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (index < chartData.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(start = 22.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------------
// KOMPONENTY POMOCNICZE
// ------------------------------------------------------------------------
@Composable
fun FinanceTimeChart(
    data: List<SubscriptionViewModel.TimeChartEntry>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    val maxAmount = data.maxOfOrNull { it.amount }?.takeIf { it > 0 } ?: 1.0
    val locale = LocalLocale.current.platformLocale

    var animationTriggered by remember { mutableStateOf(false) }
    val animateProgress by animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "time_chart_animation"
    )

    LaunchedEffect(Unit) {
        animationTriggered = true
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val availableWidth = size.width
                val spacing = 32f
                val totalSpacing = spacing * (data.size - 1)
                val barWidth = (availableWidth - totalSpacing) / data.size

                data.forEachIndexed { i, e ->
                    val barHeight = (e.amount / maxAmount * size.height).toFloat() * animateProgress
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(i * (barWidth + spacing), size.height - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(12f, 12f)
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { e ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = e.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = String.format(locale, stringResource(id = R.string.currency_format_integer_pln), e.amount),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}