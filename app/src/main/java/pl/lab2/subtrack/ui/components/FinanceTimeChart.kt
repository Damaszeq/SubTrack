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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            text = "Suma wydatków w czasie",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Główna przestrzeń wykresu
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val barCount = data.size
                if (barCount == 0) return@Canvas

                // Obliczanie szerokości słupków i odstępów
                val totalSpacing = (barCount - 1) * 24f // 24px odstępu między słupkami
                val barWidth = (canvasWidth - totalSpacing) / barCount

                data.forEachIndexed { index, entry ->
                    // Proporcjonalna wysokość słupka względem maxAmount
                    val barHeight = (entry.amount / maxAmount) * canvasHeight

                    val xOffset = index * (barWidth + 24f)
                    val yOffset = canvasHeight - barHeight.toFloat()

                    // Rysujemy zaokrąglony słupek (tylko góra zaokrąglona)
                    drawRoundRect(
                        color = if (entry.amount > 0) barColor else barColor.copy(alpha = 0.15f),
                        topLeft = Offset(xOffset, yOffset),
                        size = Size(barWidth, barHeight.toFloat()),
                        cornerRadius = CornerRadius(12f, 12f)
                    )
                }
            }
        }

        // Dolna oś z etykietami (Miesiące) i kwotami pod słupkami
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
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
                        text = "${entry.amount.toInt()} zł",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }
        }
    }
}