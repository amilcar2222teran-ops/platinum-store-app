package com.ucompensar.platinum.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucompensar.platinum.R
import com.ucompensar.platinum.ui.theme.*

@Composable
fun RoleSelectionScreen(
    modifier: Modifier = Modifier,
    onAdminClick: () -> Unit = {},
    onVendorClick: () -> Unit = {},
    onUserClick: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Imagen de Fondo
        Image(
            painter = painterResource(id = R.drawable.fondo_login),
            contentDescription = "Imagen de fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Capa translúcida para contraste
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        // Contenedor principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "PLATINUM",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = CloudWhite
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Selecciona tu perfil para ingresar",
                style = MaterialTheme.typography.titleMedium,
                color = CloudWhite.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Botón Administrador
            RoleCard(
                title = "Administrador",
                subtitle = "Gestión total del sistema",
                icon = Icons.Default.AdminPanelSettings,
                buttonColor = Blue1,
                onClick = onAdminClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Vendedor
            RoleCard(
                title = "Vendedor",
                subtitle = "Gestión de ventas y productos",
                icon = Icons.Default.Storefront,
                buttonColor = Teal,
                onClick = onVendorClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Usuario
            RoleCard(
                title = "Usuario",
                subtitle = "Acceso a la tienda y compras",
                icon = Icons.Default.Person,
                buttonColor = Purple,
                onClick = onUserClick
            )
        }
    }
}

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    buttonColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = buttonColor,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CloudWhite
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CloudWhite.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionScreenPreview() {
    PlatinumTheme {
        RoleSelectionScreen()
    }
}
