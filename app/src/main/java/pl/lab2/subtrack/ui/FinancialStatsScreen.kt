package pl.lab2.subtrack.ui

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
    val subData by viewModel.pieChartData.collectAsState()
    val catData by viewModel.categoryChartData.collectAsState()
    val timeData by viewModel.timeChartData.collectAsState()
    val currentPeriod by viewModel.selectedPeriod.collectAsState()

    val currentView = viewModel.currentViewType
    val chartData = if (currentView == StatsViewType.BY_SUBSCRIPTION) subData else catData
    val totalMonthlySpending = chartData.sumOf { it.value }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.stats_title),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineLarge
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
            item {
                Text(
                    text = stringResource(id = R.string.stats_history),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp, start = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SubscriptionViewModel.TimePeriod.values().forEach { period ->
                        FilterChip(
                            selected = currentPeriod == period,
                            onClick = { viewModel.changeTimePeriod(period) },
                            label = { Text(text = period.label, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                FinanceTimeChart(data = timeData, modifier = Modifier.fillMaxWidth().padding(bottom = 28.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)).padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val activeColor = MaterialTheme.colorScheme.background

                    Button(
                        onClick = { viewModel.toggleViewType(StatsViewType.BY_SUBSCRIPTION) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (currentView == StatsViewType.BY_SUBSCRIPTION) activeColor else Color.Transparent),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text(stringResource(id = R.string.stats_tab_subscriptions), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { viewModel.toggleViewType(StatsViewType.BY_CATEGORY) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (currentView == StatsViewType.BY_CATEGORY) activeColor else Color.Transparent),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text(stringResource(id = R.string.stats_tab_categories), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), contentAlignment = Alignment.Center) {
                    FinancePieChart(data = chartData, totalAmount = totalMonthlySpending, modifier = Modifier.size(240.dp))
                }
            }

            item {
                val mostExpensive = remember(chartData) { chartData.maxByOrNull { it.value } }
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp).clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)).padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = stringResource(id = R.string.stats_yearly_forecast), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Text(text = String.format(LocalLocale.current.platformLocale, "%.2f zł", totalMonthlySpending * 12), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = if (currentView == StatsViewType.BY_SUBSCRIPTION) stringResource(id = R.string.stats_active_subs) else stringResource(id = R.string.stats_used_cats), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Text(text = "${chartData.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (mostExpensive != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = if (currentView == StatsViewType.BY_SUBSCRIPTION) stringResource(id = R.string.stats_highest_cost) else stringResource(id = R.string.stats_most_expensive_cat), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                Text(text = mostExpensive.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            }
                            Text(text = String.format(LocalLocale.current.platformLocale, "%.2f zł / mc", mostExpensive.value), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = if (currentView == StatsViewType.BY_SUBSCRIPTION) stringResource(id = R.string.stats_subs_structure) else stringResource(id = R.string.stats_cat_structure), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
                }
            }

            itemsIndexed(chartData.sortedByDescending { it.value }) { index, entry ->
                val percentage = if (totalMonthlySpending > 0) (entry.value / totalMonthlySpending) * 100 else 0.0
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(width = 6.dp, height = 24.dp).clip(CircleShape).background(entry.color))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = entry.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text(text = String.format(LocalLocale.current.platformLocale, stringResource(id = R.string.stats_percent_format), percentage), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(text = String.format(LocalLocale.current.platformLocale, "%.2f zł", entry.value), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                    if (index < chartData.lastIndex) HorizontalDivider(modifier = Modifier.padding(start = 22.dp))
                }
            }
        }
    }
}

@Composable
fun FinanceTimeChart(data: List<SubscriptionViewModel.TimeChartEntry>, modifier: Modifier = Modifier, barColor: Color = MaterialTheme.colorScheme.primary) {
    val maxAmount = data.maxOfOrNull { it.amount }?.takeIf { it > 0 } ?: 1.0
    val locale = LocalLocale.current.platformLocale

    Column(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(16.dp)).padding(16.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barWidth = (size.width - (data.size - 1) * 32f) / data.size
                data.forEachIndexed { i, e ->
                    drawRoundRect(barColor, Offset(i * (barWidth + 32f), size.height - (e.amount / maxAmount * size.height).toFloat()), Size(barWidth, (e.amount / maxAmount * size.height).toFloat()), CornerRadius(12f, 12f))
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            data.forEach { e ->
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = e.label, style = MaterialTheme.typography.labelSmall)
                    Text(text = String.format(locale, "%.0f zł", e.amount), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}