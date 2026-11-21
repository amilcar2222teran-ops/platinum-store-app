package com.ucompensar.platinum.ui.screens

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.ucompensar.platinum.R
import com.ucompensar.platinum.data.service.FirestoreService
import com.ucompensar.platinum.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: (Int) -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    fun launchBiometricAuth() {
        if (activity == null) {
            errorMessage = "No se pudo iniciar la autenticación biométrica."
            return
        }

        val biometricManager = BiometricManager.from(activity)
        val canAuthenticate = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        )

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            errorMessage = "El dispositivo no tiene huella dactilar registrada o no soporta biometría."
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación Biométrica")
            .setSubtitle("Usa tu huella dactilar para iniciar sesión en Platinum")
            .setNegativeButtonText("Cancelar")
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    com.ucompensar.platinum.data.session.UserSession.currentUserEmail = "huella@platinum.app"
                    com.ucompensar.platinum.data.session.UserSession.currentUserId = "huella_user"
                    com.ucompensar.platinum.data.session.UserSession.currentUserRole = 3
                    onLoginSuccess(3)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    errorMessage = errString.toString()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    errorMessage = "Huella no reconocida. Intenta nuevamente."
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }

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
                    onValueChange = {
                        email = it
                        errorMessage = null
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

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
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

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Olvidaste tu contraseña
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onForgotPasswordClick,
                        enabled = !isLoading,
                        colors = ButtonDefaults.textButtonColors(contentColor = CloudWhite)
                    ) {
                        Text("¿Olvidaste tu contraseña?")
                    }
                }

                // Mensaje de Error
                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botón de Iniciar Sesión
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Por favor ingrese correo y contraseña"
                            return@Button
                        }
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            val result = FirestoreService.authenticateUser(email, password)
                            isLoading = false
                            result.fold(
                                onSuccess = { role ->
                                    com.ucompensar.platinum.data.session.UserSession.currentUserEmail = email
                                    com.ucompensar.platinum.data.session.UserSession.currentUserId = email
                                    com.ucompensar.platinum.data.session.UserSession.currentUserRole = role
                                    onLoginSuccess(role)
                                },
                                onFailure = { ex ->
                                    errorMessage = ex.message ?: "Error al iniciar sesión"
                                }
                            )
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue1,
                        contentColor = CloudWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = CloudWhite,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Iniciar Sesión",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CloudWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para Ingreso con Huella Dactilar
                IconButton(
                    onClick = { launchBiometricAuth() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .size(52.dp)
                        .background(Teal.copy(alpha = 0.25f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Ingresar con Huella Dactilar",
                        tint = Teal,
                        modifier = Modifier.size(36.dp)
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
                        enabled = !isLoading,
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
