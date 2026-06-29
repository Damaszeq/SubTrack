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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.lab2.subtrack.ui.PieChartEntry

@Composable
fun FinancePieChart(
    data: List<PieChartEntry>,
    totalAmount: Double,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }

    // Profesjonalna krzywa animacji stosowana w aplikacjach Google/Fintech
    val animateProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize() // Pozwalamy na elastyczne dopasowanie do kontenera z ekranu
                .padding(12.dp)
        ) {
            val strokeWidthPx = 16.dp.toPx() // Nowoczesna, smuklejsza grubość linii
            val totalValues = data.sumOf { it.value }.toFloat()

            if (totalValues == 0f) {
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.2f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx)
                )
                return@Canvas
            }

            var startAngle = -90f // Start od samej góry

            // Definiujemy odstęp (szczelinę) między wycinkami w stopniach
            // Jeśli jest tylko jedna subskrypcja, nie robimy przerw
            val gapAngle = if (data.size > 1) 3f else 0f

            data.forEach { entry ->
                val rawSweepAngle = (entry.value.toFloat() / totalValues) * 360f
                // Odejmujemy szczelinę od całkowitego kąta segmentu, by nie zachodziły na siebie
                val sweepAngle = (rawSweepAngle - gapAngle) * animateProgress

                if (sweepAngle > 0f) {
                    drawArc(
                        color = entry.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidthPx,
                            cap = StrokeCap.Round // Zaokrąglone końce segmentów
                        )
                    )
                }
                // Przesuwamy kąt startowy o pełną wartość, zachowując szczelinę w pamięci
                startAngle += rawSweepAngle
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MIESIĘCZNIE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified // Lub np. 1.5.sp po zaimportowaniu
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = String.format(LocalLocale.current.platformLocale, "%.2f zł", totalAmount),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
