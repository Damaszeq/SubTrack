package pl.lab2.subtrack.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R
import pl.lab2.subtrack.ui.SubscriptionViewModel

@Composable
fun FinanceTimeChart(
    data: List<SubscriptionViewModel.TimeChartEntry>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    val maxAmount = data.maxOfOrNull { it.amount }?.takeIf { it > 0 } ?: 1.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.chart_title_spending_over_time),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barCount = data.size
                if (barCount == 0) return@Canvas

                val totalSpacing = (barCount - 1) * 24f
                val barWidth = (size.width - totalSpacing) / barCount

                data.forEachIndexed { index, entry ->
                    val barHeight = (entry.amount / maxAmount) * size.height
                    val xOffset = index * (barWidth + 24f)
                    val yOffset = size.height - barHeight.toFloat()

                    drawRoundRect(
                        color = if (entry.amount > 0) barColor else barColor.copy(alpha = 0.15f),
                        topLeft = Offset(xOffset, yOffset),
                        size = Size(barWidth, barHeight.toFloat()),
                        cornerRadius = CornerRadius(12f, 12f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { entry ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = entry.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(id = R.string.currency_format, entry.amount.toInt()),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }
        }
    }
}