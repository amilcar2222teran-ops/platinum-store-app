package com.ucompensar.platinum.ui.screens.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucompensar.platinum.data.model.OrderData
import com.ucompensar.platinum.data.service.FirestoreService
import com.ucompensar.platinum.ui.theme.CloudWhite
import com.ucompensar.platinum.ui.theme.PlatinumTheme
import com.ucompensar.platinum.ui.theme.Purple
import com.ucompensar.platinum.ui.theme.Teal
import kotlinx.coroutines.launch

@Composable
fun SellHistoryScreen(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit = {}
) {
    var orders by remember { mutableStateOf<List<OrderData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val result = FirestoreService.getOrders()
            isLoading = false
            result.onSuccess { list ->
                orders = list
            }
        }
    }

    val barColors = listOf(
        Color(0xFF4A89F3), // Azul
        Color(0xFFA58CF5), // Morado claro
        Color(0xFFF6B26B), // Naranja / Dorado
        Color(0xFFFCDD53)  // Amarillo
    )

    val topBarData = remember(orders) {
        val aggregated = orders.flatMap { it.items }
            .groupBy { it.nombre.ifBlank { "Producto" } }
            .mapValues { entry -> entry.value.sumOf { it.cantidad } }
            .entries
            .sortedByDescending { it.value }
            .take(4)

        if (aggregated.isEmpty()) {
            listOf(
                BarData(label = "1", value = 60f, color = barColors[0]),
                BarData(label = "2", value = 45f, color = barColors[1]),
                BarData(label = "3", value = 78f, color = barColors[2]),
                BarData(label = "4", value = 30f, color = barColors[3])
            )
        } else {
            aggregated.mapIndexed { index, entry ->
                BarData(
                    label = "${index + 1}",
                    value = entry.value.toFloat(),
                    color = barColors[index % barColors.size]
                )
            }
        }
    }

    val recentSalesList = remember(orders) {
        val sortedOrders = orders.sortedByDescending { it.fecha }
        val items = sortedOrders.flatMap { order -> order.items }.take(5)

        if (items.isEmpty()) {
            listOf(
                "1.........................",
                "2.........................",
                "3.........................",
                "4.........................",
                "5........................."
            )
        } else {
            val result = items.mapIndexed { index, item ->
                "${index + 1}. ${item.nombre} (\$" + "${item.precio.toInt()})"
            }.toMutableList()

            while (result.size < 5) {
                result.add("${result.size + 1}.........................")
            }
            result
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Purple)
            .padding(24.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Teal)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = CloudWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar Sesión",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CloudWhite,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

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
                    barData = topBarData,
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
                    recentSalesList.forEach { item ->
                        Text(
                            text = item,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
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
}

@Composable
private fun SalesBarChart(
    barData: List<BarData>,
    modifier: Modifier = Modifier
) {
    val maxVal = maxOf(80f, barData.maxOfOrNull { it.value } ?: 80f)
    val yAxisLabels = listOf(
        maxVal.toInt().toString(),
        (maxVal * 0.75f).toInt().toString(),
        (maxVal * 0.5f).toInt().toString(),
        (maxVal * 0.25f).toInt().toString(),
        "0"
    )

    Row(
        modifier = modifier
    ) {
        // Eje Y
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

        // Área del gráfico
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
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

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    barData.forEach { data ->
                        val barHeightFraction = (data.value / maxVal).coerceIn(0.05f, 1f)
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
