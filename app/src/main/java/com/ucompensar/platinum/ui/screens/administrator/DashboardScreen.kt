package com.ucompensar.platinum.ui.screens.administrator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucompensar.platinum.ui.theme.Blue1
import com.ucompensar.platinum.ui.theme.Purple
import com.ucompensar.platinum.ui.theme.CloudWhite
import com.ucompensar.platinum.ui.theme.PlatinumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onUserClick: () -> Unit = {},
    onProductClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Purple)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = "¡Bienvenido, Administrador!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CloudWhite
            )

            Text(
                text = "Selecciona una opción de gestión.",
                style = MaterialTheme.typography.bodyMedium,
                color = CloudWhite
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Grilla de opciones
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard(
                    title = "Usuarios",
                    icon = Icons.Default.Group,
                    color = Blue1,
                    modifier = Modifier.weight(0.3f),
                    onClick = onUserClick
                )
                DashboardCard(
                    title = "Productos",
                    icon = Icons.Default.Inventory,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(0.3f),
                    onClick = onProductClick
                )
                DashboardCard(
                    title = "Reportes",
                    icon = Icons.Default.Analytics,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(0.3f),
                    onClick = onReportClick
                )
            }

        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CloudWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    PlatinumTheme {
        DashboardScreen()
    }
}
