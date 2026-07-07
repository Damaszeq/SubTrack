package pl.lab2.subtrack.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.R
import pl.lab2.subtrack.ui.PieChartEntry

@Composable
fun FinancePieChart(
    data: List<PieChartEntry>,
    totalAmount: Double,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }

    val animateProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "pie_chart_animation"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val strokeWidthPx = 16.dp.toPx()
            val totalValues = data.sumOf { it.value }.toFloat()

            if (totalValues == 0f) {
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.2f),
                    startAngle = 0f, sweepAngle = 360f, useCenter = false,
                    style = Stroke(width = strokeWidthPx)
                )
                return@Canvas
            }

            var startAngle = -90f
            val gapAngle = if (data.size > 1) 3f else 0f

            data.forEach { entry ->
                val rawSweepAngle = (entry.value.toFloat() / totalValues) * 360f
                val sweepAngle = (rawSweepAngle - gapAngle) * animateProgress

                if (sweepAngle > 0f) {
                    drawArc(
                        color = entry.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )
                }
                startAngle += rawSweepAngle
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                text = stringResource(id = R.string.pie_chart_label_monthly),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = String.format(LocalLocale.current.platformLocale, stringResource(id = R.string.currency_format_pie), totalAmount),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}