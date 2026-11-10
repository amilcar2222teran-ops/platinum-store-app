package com.ucompensar.platinum.ui.screens.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucompensar.platinum.ui.theme.CloudWhite
import com.ucompensar.platinum.ui.theme.PlatinumTheme
import com.ucompensar.platinum.ui.theme.Purple

@Composable
fun SellHistoryScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Purple)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Título: Top Ventas Mes
            Text(
                text = "Top Ventas Mes",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = CloudWhite,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Gráfica de Barras
            SalesBarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Título: Ultimas Ventas
            Text(
                text = "Ultimas Ventas",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = CloudWhite,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de Últimas Ventas
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val items = listOf(
                    "1.........................",
                    "2.........................",
                    "3.........................",
                    "4.........................",
                    "5........................."
                )
                items.forEach { item ->
                    Text(
                        text = item,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = CloudWhite,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SalesBarChart(
    modifier: Modifier = Modifier
) {
    val barData = listOf(
        BarData(label = "1", value = 60f, color = Color(0xFF4A89F3)),
        BarData(label = "2", value = 45f, color = Color(0xFFA58CF5)),
        BarData(label = "3", value = 78f, color = Color(0xFFF6B26B)),
        BarData(label = "4", value = 30f, color = Color(0xFFFCDD53))
    )

    val yAxisLabels = listOf("80", "60", "40", "20", "0")
    val maxValue = 80f

    Row(
        modifier = modifier
    ) {
        // Eje Y (Etiquetas 80, 60, 40, 20, 0)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            yAxisLabels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = CloudWhite.copy(alpha = 0.7f)
                )
            }
        }

        // Área del gráfico con líneas de fondo y barras
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // Contenedor de barras y líneas guía
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Líneas horizontales de fondo
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(5) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(CloudWhite.copy(alpha = 0.15f))
                        )
                    }
                }

                // Barras
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    barData.forEach { data ->
                        val barHeightFraction = (data.value / maxValue).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(barHeightFraction)
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                .background(data.color)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Eje X (Etiquetas 1, 2, 3, 4)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                barData.forEach { data ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = data.label,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = CloudWhite,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private data class BarData(
    val label: String,
    val value: Float,
    val color: Color
)

@Preview(showBackground = true)
@Composable
fun SellHistoryScreenPreview() {
    PlatinumTheme {
        SellHistoryScreen()
    }
}
