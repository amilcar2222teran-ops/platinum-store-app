package com.ucompensar.platinum.ui.screens.administrator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucompensar.platinum.data.model.UserData
import com.ucompensar.platinum.data.service.FirestoreService
import com.ucompensar.platinum.ui.theme.CloudWhite
import com.ucompensar.platinum.ui.theme.PlatinumTheme
import com.ucompensar.platinum.ui.theme.Purple
import com.ucompensar.platinum.ui.theme.Teal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAdministrationScreen(
    modifier: Modifier = Modifier,
    userToEdit: UserData? = null,
    onSaveSuccess: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var userId by remember(userToEdit) { mutableStateOf(userToEdit?.id ?: "") }
    var email by remember(userToEdit) { mutableStateOf(userToEdit?.correo ?: "") }
    var password by remember(userToEdit) { mutableStateOf(userToEdit?.contrasena ?: "") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Rol seleccionado: 1 = Administrador, 2 = Vendedor, 3 = Usuario
    var selectedRole by remember(userToEdit) { mutableIntStateOf(userToEdit?.rol ?: 3) }
    var isRoleDropdownExpanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessStatus by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val roleOptions = listOf(
        1 to "1 - Administrador",
        2 to "2 - Vendedor",
        3 to "3 - Usuario"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Purple)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = if (userToEdit != null) "Editar Usuario" else "Crear Nuevo Usuario",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CloudWhite,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de Correo Electrónico
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    statusMessage = null
                },
                label = { Text("Correo Electrónico") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Icono email"
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
                    statusMessage = null
                },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Icono contraseña"
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

            Spacer(modifier = Modifier.height(16.dp))

            // Selección de Rol (Únicamente 1, 2 o 3)
            ExposedDropdownMenuBox(
                expanded = isRoleDropdownExpanded,
                onExpandedChange = { if (!isLoading) isRoleDropdownExpanded = !isRoleDropdownExpanded }
            ) {
                val selectedLabel = roleOptions.firstOrNull { it.first == selectedRole }?.second ?: "3 - Usuario"
                OutlinedTextField(
                    value = selectedLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rol (1, 2 o 3)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Icono rol"
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleDropdownExpanded)
                    },
                    enabled = !isLoading,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = isRoleDropdownExpanded,
                    onDismissRequest = { isRoleDropdownExpanded = false }
                ) {
                    roleOptions.forEach { (roleValue, roleText) ->
                        DropdownMenuItem(
                            text = { Text(roleText) },
                            onClick = {
                                selectedRole = roleValue
                                isRoleDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Mensaje de estado (éxito o error)
            statusMessage?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = msg,
                    color = if (isSuccessStatus) Teal else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botón Guardar / Actualizar
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        statusMessage = "Por favor complete todos los campos"
                        isSuccessStatus = false
                        return@Button
                    }

                    isLoading = true
                    statusMessage = null

                    val userToSave = UserData(
                        id = userId,
                        correo = email,
                        contrasena = password,
                        rol = selectedRole
                    )

                    coroutineScope.launch {
                        val result = FirestoreService.saveOrUpdateUser(userToSave)
                        isLoading = false
                        result.fold(
                            onSuccess = {
                                isSuccessStatus = true
                                statusMessage = "¡Usuario guardado exitosamente!"
                                onSaveSuccess()
                            },
                            onFailure = { ex ->
                                isSuccessStatus = false
                                statusMessage = ex.localizedMessage ?: "Error al guardar el usuario"
                            }
                        )
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal,
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
                        text = if (userToEdit != null) "Actualizar" else "Guardar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CloudWhite
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserAdministrationScreenPreview() {
    PlatinumTheme {
        UserAdministrationScreen()
    }
}
