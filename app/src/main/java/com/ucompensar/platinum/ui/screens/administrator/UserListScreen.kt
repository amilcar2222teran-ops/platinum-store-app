package com.ucompensar.platinum.ui.screens.administrator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucompensar.platinum.data.model.UserData
import com.ucompensar.platinum.data.service.FirestoreService
import com.ucompensar.platinum.ui.theme.CloudWhite
import com.ucompensar.platinum.ui.theme.PlatinumTheme
import com.ucompensar.platinum.ui.theme.Purple
import com.ucompensar.platinum.ui.theme.Teal
import kotlinx.coroutines.launch

@Composable
fun UserListScreen(
    modifier: Modifier = Modifier,
    onUserClick: (UserData) -> Unit = {},
    onCreateUserClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var users by remember { mutableStateOf<List<UserData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    fun loadUsers() {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            val result = FirestoreService.getUsers()
            isLoading = false
            result.fold(
                onSuccess = { list ->
                    users = list
                },
                onFailure = { ex ->
                    errorMessage = ex.localizedMessage ?: "Error al cargar la lista de usuarios"
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        loadUsers()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Purple)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

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

            Text(
                text = "Lista de Usuarios",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = CloudWhite
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Teal)
                } else if (errorMessage != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { loadUsers() },
                            colors = ButtonDefaults.buttonColors(containerColor = Teal)
                        ) {
                            Text("Reintentar", color = CloudWhite)
                        }
                    }
                } else if (users.isEmpty()) {
                    Text(
                        text = "No hay usuarios registrados",
                        color = CloudWhite.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(users) { user ->
                            Card(
                                onClick = { onUserClick(user) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = CloudWhite.copy(alpha = 0.15f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "ID: ${user.id}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = CloudWhite.copy(alpha = 0.7f),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Correo: ${user.correo}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = CloudWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                FirestoreService.deleteUser(user.id)
                                                loadUsers()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Borrar usuario",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón en la parte inferior para crear un nuevo usuario
            Button(
                onClick = onCreateUserClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal,
                    contentColor = CloudWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Crear Nuevo Usuario",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CloudWhite
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserListScreenPreview() {
    PlatinumTheme {
        UserListScreen()
    }
}
