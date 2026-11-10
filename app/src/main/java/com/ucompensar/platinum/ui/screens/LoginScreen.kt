package com.ucompensar.platinum.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucompensar.platinum.R
import com.ucompensar.platinum.ui.theme.*

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

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

        // 3. Tarjeta contenedora transparente
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
                // Título principal usando CloudWhite
                Text(
                    text = "PLATINUM",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = CloudWhite
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.titleMedium,
                    color = CloudWhite.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Campo de Correo Electrónico
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Icono Correo"
                        )
                    },
                    singleLine = true,
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

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
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

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Olvidaste tu contraseña
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onForgotPasswordClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = CloudWhite)
                    ) {
                        Text("¿Olvidaste tu contraseña?")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botón de Iniciar Sesión
                Button(
                    onClick = { onLoginClick(email, password) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue1,
                        contentColor = CloudWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CloudWhite
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Enlace para Registrarse
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿No tienes una cuenta?",
                        color = CloudWhite.copy(alpha = 0.9f)
                    )
                    TextButton(
                        onClick = onRegisterClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = Teal)
                    ) {
                        Text(
                            text = "Regístrate",
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
fun LoginScreenPreview() {
    PlatinumTheme {
        LoginScreen()
    }
}
