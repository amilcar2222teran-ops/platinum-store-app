package com.ucompensar.platinum.ui.screens.administrator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucompensar.platinum.data.model.ProductData
import com.ucompensar.platinum.data.service.FirestoreService
import com.ucompensar.platinum.ui.theme.CloudWhite
import com.ucompensar.platinum.ui.theme.PlatinumTheme
import com.ucompensar.platinum.ui.theme.Purple
import com.ucompensar.platinum.ui.theme.Teal
import kotlinx.coroutines.launch

@Composable
fun ProductAdministrationScreen(
    modifier: Modifier = Modifier,
    productToEdit: ProductData? = null,
    onSaveSuccess: () -> Unit = {}
) {
    val context = LocalContext.current

    var productId by remember(productToEdit) { mutableStateOf(productToEdit?.id ?: "") }
    var name by remember(productToEdit) { mutableStateOf(productToEdit?.nombre ?: "") }
    var tag by remember(productToEdit) { mutableStateOf(productToEdit?.tag ?: "") }
    var sizes by remember(productToEdit) { mutableStateOf(productToEdit?.sizes ?: "") }
    var description by remember(productToEdit) { mutableStateOf(productToEdit?.descripcion ?: "") }
    var materials by remember(productToEdit) { mutableStateOf(productToEdit?.materiales ?: "") }
    var price by remember(productToEdit) { mutableStateOf(if (productToEdit != null && productToEdit.precio > 0) productToEdit.precio.toString() else "") }
    var quantity by remember(productToEdit) { mutableStateOf(if (productToEdit != null && productToEdit.cantidad > 0) productToEdit.cantidad.toString() else "") }
    var existingImageUrl by remember(productToEdit) { mutableStateOf(productToEdit?.imagenUrl ?: "") }

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showImagePickerDialog by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessStatus by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Launcher para seleccionar imagen de la Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            capturedBitmap = null
        }
    }

    // Launcher para tomar foto con la Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            selectedUri = null
        }
    }

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
            Spacer(modifier = Modifier.height(20.dp))

            // 1. Fila Superior (Marco Imagen + Campos Nombre/Tag/Sizes)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Marco contenedor de Imagen interactivo
                Surface(
                    modifier = Modifier
                        .size(170.dp)
                        .clickable(enabled = !isLoading) {
                            showImagePickerDialog = true
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = CloudWhite.copy(alpha = 0.15f),
                    border = BorderStroke(2.dp, Teal)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val displayBitmap = capturedBitmap ?: selectedUri?.let { loadBitmapFromUri(context, it) }

                        if (displayBitmap != null) {
                            Image(
                                bitmap = displayBitmap.asImageBitmap(),
                                contentDescription = "Imagen del producto",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
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
                }

                // Columna con los campos Nombre, Tag y Sizes a la derecha
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            statusMessage = null
                        },
                        label = { Text("Nombre") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Abc,
                                contentDescription = "Icono Nombre"
                            )
                        },
                        singleLine = true,
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
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = tag,
                        onValueChange = {
                            tag = it
                            statusMessage = null
                        },
                        label = { Text("Tag") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Sell,
                                contentDescription = "Icono Tag"
                            )
                        },
                        singleLine = true,
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
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = sizes,
                        onValueChange = {
                            sizes = it
                            statusMessage = null
                        },
                        label = { Text("Sizes") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Straighten,
                                contentDescription = "Icono sizes"
                            )
                        },
                        singleLine = true,
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
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Campo de Descripción
            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    statusMessage = null
                },
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
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Campo de Materiales
            OutlinedTextField(
                value = materials,
                onValueChange = {
                    materials = it
                    statusMessage = null
                },
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
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 4. Campos de Precio y Cantidad en fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        price = it
                        statusMessage = null
                    },
                    label = { Text("Precio") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Icono price"
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    onValueChange = {
                        quantity = it
                        statusMessage = null
                    },
                    label = { Text("Cantidad") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = "Icono quantity"
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

            // Mensaje de estado (éxito o error)
            statusMessage?.let { msg ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = msg,
                    color = if (isSuccessStatus) Teal else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 5. Botón Guardar / Actualizar
            Button(
                onClick = {
                    if (name.isBlank() || price.isBlank()) {
                        statusMessage = "Por favor ingrese al menos el nombre y el precio"
                        isSuccessStatus = false
                        return@Button
                    }

                    val parsedPrice = price.toDoubleOrNull() ?: 0.0
                    val parsedQuantity = quantity.toIntOrNull() ?: 0

                    isLoading = true
                    statusMessage = null

                    val productToSave = ProductData(
                        id = productId,
                        nombre = name,
                        tag = tag,
                        sizes = sizes,
                        descripcion = description,
                        materiales = materials,
                        precio = parsedPrice,
                        cantidad = parsedQuantity,
                        imagenUrl = existingImageUrl
                    )

                    coroutineScope.launch {
                        val result = FirestoreService.saveOrUpdateProduct(
                            product = productToSave,
                            imageBitmap = capturedBitmap,
                            imageUri = selectedUri
                        )
                        isLoading = false
                        result.fold(
                            onSuccess = {
                                isSuccessStatus = true
                                statusMessage = "¡Producto guardado exitosamente!"
                                onSaveSuccess()
                            },
                            onFailure = { ex ->
                                isSuccessStatus = false
                                statusMessage = ex.localizedMessage ?: "Error al guardar el producto"
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
                        text = if (productToEdit != null) "Actualizar" else "Guardar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CloudWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Diálogo para seleccionar el origen de la imagen (Cámara o Galería)
    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Seleccionar Imagen") },
            text = { Text("Elige de dónde quieres cargar la imagen del producto:") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImagePickerDialog = false
                        cameraLauncher.launch(null)
                    }
                ) {
                    Text("Cámara")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImagePickerDialog = false
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text("Galería")
                }
            }
        )
    }
}

private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        null
    }
}

@Preview(showBackground = true)
@Composable
fun ProductAdministrationScreenPreview() {
    PlatinumTheme {
        ProductAdministrationScreen()
    }
}
