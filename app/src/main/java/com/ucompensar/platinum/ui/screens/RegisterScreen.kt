package com.ucompensar.platinum.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucompensar.platinum.R
import com.ucompensar.platinum.data.service.FirestoreService
import com.ucompensar.platinum.ui.theme.Blue1
import com.ucompensar.platinum.ui.theme.CloudWhite
import com.ucompensar.platinum.ui.theme.PlatinumTheme
import com.ucompensar.platinum.ui.theme.Teal
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegisterSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessStatus by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 1. Imagen de Fondo
        Image(
            painter = painterResource(id = R.drawable.fondo_login),
            contentDescription = "Imagen de fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Capa translúcida para contraste
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        // 3. Tarjeta contenedora
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                    text = "Crear Nueva Cuenta",
                    style = MaterialTheme.typography.titleMedium,
                    color = CloudWhite.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo Correo Electrónico
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        statusMessage = null
                    },
                    label = { Text("Correo electrónico") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Icono Correo"
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CloudWhite,
                        unfocusedTextColor = CloudWhite,
                        focusedBorderColor = Teal,
                        unfocusedBorderColor = CloudWhite.copy(alpha = 0.7f),
                        focusedLabelColor = Teal,
                        unfocusedLabelColor = CloudWhite.copy(alpha = 0.8f),
                        focusedLeadingIconColor = Teal,
                        unfocusedLeadingIconColor = CloudWhite.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Campo Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        statusMessage = null
                    },
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Icono Contraseña"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    singleLine = true,
                    enabled = !isLoading,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CloudWhite,
                        unfocusedTextColor = CloudWhite,
                        focusedBorderColor = Teal,
                        unfocusedBorderColor = CloudWhite.copy(alpha = 0.7f),
                        focusedLabelColor = Teal,
                        unfocusedLabelColor = CloudWhite.copy(alpha = 0.8f),
                        focusedLeadingIconColor = Teal,
                        unfocusedLeadingIconColor = CloudWhite.copy(alpha = 0.8f),
                        focusedTrailingIconColor = Teal,
                        unfocusedTrailingIconColor = CloudWhite.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Campo Confirmar Contraseña
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        statusMessage = null
                    },
                    label = { Text("Confirmar contraseña") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Icono Confirmar Contraseña"
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CloudWhite,
                        unfocusedTextColor = CloudWhite,
                        focusedBorderColor = Teal,
                        unfocusedBorderColor = CloudWhite.copy(alpha = 0.7f),
                        focusedLabelColor = Teal,
                        unfocusedLabelColor = CloudWhite.copy(alpha = 0.8f),
                        focusedLeadingIconColor = Teal,
                        unfocusedLeadingIconColor = CloudWhite.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Mensaje de estado
                statusMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = msg,
                        color = if (isSuccessStatus) Teal else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Registrarse
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                            statusMessage = "Por favor complete todos los campos"
                            isSuccessStatus = false
                            return@Button
                        }
                        if (password != confirmPassword) {
                            statusMessage = "Las contraseñas no coinciden"
                            isSuccessStatus = false
                            return@Button
                        }

                        isLoading = true
                        statusMessage = null

                        coroutineScope.launch {
                            // Se registra automáticamente con el ROL 3 (Usuario normal)
                            val result = FirestoreService.saveUser(email, password, role = 3)
                            isLoading = false
                            result.fold(
                                onSuccess = {
                                    isSuccessStatus = true
                                    statusMessage = "¡Cuenta creada exitosamente! Inicia sesión."
                                    onRegisterSuccess()
                                },
                                onFailure = { ex ->
                                    isSuccessStatus = false
                                    statusMessage = ex.localizedMessage ?: "Error al registrar la cuenta"
                                }
                            )
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Registrarse",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Volver a Iniciar Sesión
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Ya tienes una cuenta?",
                        color = CloudWhite.copy(alpha = 0.9f)
                    )
                    TextButton(
                        onClick = onLoginClick,
                        enabled = !isLoading,
                        colors = ButtonDefaults.textButtonColors(contentColor = Blue1)
                    ) {
                        Text(
                            text = "Inicia Sesión",
                            fontWeight = FontWeight.Bold,
                            color = Teal
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    PlatinumTheme {
        RegisterScreen()
    }
}
