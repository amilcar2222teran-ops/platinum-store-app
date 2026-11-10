package com.ucompensar.platinum.ui.screens.administrator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucompensar.platinum.ui.theme.Blue1
import com.ucompensar.platinum.ui.theme.CloudWhite
import com.ucompensar.platinum.ui.theme.PlatinumTheme
import com.ucompensar.platinum.ui.theme.Purple
import com.ucompensar.platinum.ui.theme.Teal

@Composable
fun ProductAdministrationScreen(
    modifier: Modifier = Modifier,
    onSaveClick: () -> Unit = {},
    onPublishClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var sizes by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var materials by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Purple)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // 1. Fila Superior (Marco Imagen + Campos Nombre/Tag/Sizes)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Marco contenedor de Imagen
                Surface(
                    modifier = Modifier.size(170.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = CloudWhite.copy(alpha = 0.15f),
                    border = BorderStroke(2.dp, Teal)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Agregar imagen",
                                tint = CloudWhite,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Imagen",
                                style = MaterialTheme.typography.labelMedium,
                                color = CloudWhite.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Columna con los campos Nombre, Tag y Sizes a la derecha
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Abc,
                                contentDescription = "Icono Nombre"
                            )
                        },
                        singleLine = true,
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

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = tag,
                        onValueChange = { tag = it },
                        label = { Text("Tag") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Sell,
                                contentDescription = "Icono Tag"
                            )
                        },
                        singleLine = true,
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

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = sizes,
                        onValueChange = { sizes = it },
                        label = { Text("Sizes") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Straighten,
                                contentDescription = "Icono sizes"
                            )
                        },
                        singleLine = true,
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Campo de Descripción (Label centrado y multilínea)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = {
                    Text(
                        text = "Descripción",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Icono Description"
                    )
                },
                minLines = 4,
                maxLines = 6,
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

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Campo de Materiales (Label centrado y multilínea)
            OutlinedTextField(
                value = materials,
                onValueChange = { materials = it },
                label = {
                    Text(
                        text = "Materiales",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Hardware,
                        contentDescription = "Icono Materials"
                    )
                },
                minLines = 4,
                maxLines = 6,
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
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Icono price"
                        )
                    },
                    singleLine = true,
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
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Cantidad") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = "Icono quantity"
                        )
                    },
                    singleLine = true,
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
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 4. Botones alineados uno al lado del otro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { onSaveClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal,
                        contentColor = CloudWhite
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Guardar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CloudWhite
                    )
                }

                Button(
                    onClick = { onPublishClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue1,
                        contentColor = CloudWhite
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Publicar",
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
fun ProductAdministrationScreenPreview() {
    PlatinumTheme {
        ProductAdministrationScreen()
    }
}
