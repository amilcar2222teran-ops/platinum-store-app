package com.ucompensar.platinum.ui.screens.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucompensar.platinum.data.model.ProductData
import com.ucompensar.platinum.data.service.FirestoreService
import com.ucompensar.platinum.ui.theme.CloudWhite
import com.ucompensar.platinum.ui.theme.PlatinumTheme
import com.ucompensar.platinum.ui.theme.Purple
import com.ucompensar.platinum.ui.theme.Teal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    modifier: Modifier = Modifier,
    product: ProductData? = null,
    onCartClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var cartItemCount by remember { mutableIntStateOf(0) }
    var isAddingToCart by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val currentProduct = remember(product) {
        product ?: ProductData(
            id = "1",
            nombre = "Producto 1",
            descripcion = "Descripción detallada del producto",
            materiales = "Oro, Plata, Piedras Preciosas",
            precio = 150000.0
        )
    }

    fun loadCartCount() {
        coroutineScope.launch {
            val cartResult = FirestoreService.getCartItems(com.ucompensar.platinum.data.session.UserSession.currentUserId)
            cartResult.onSuccess { items ->
                cartItemCount = items.sumOf { it.cantidad }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadCartCount()
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Barra de Búsqueda y Botón de Carrito con Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CloudWhite,
                        unfocusedContainerColor = CloudWhite,
                        disabledContainerColor = CloudWhite,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                )

                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = Teal,
                                contentColor = Color.Black
                            ) {
                                Text("$cartItemCount")
                            }
                        }
                    }
                ) {
                    IconButton(onClick = onCartClick) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Carrito de compras",
                            tint = CloudWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // 2. Imagen / Tarjeta Principal del Producto
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CloudWhite
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentProduct.nombre,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Nombre del producto
            Text(
                text = currentProduct.nombre,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = CloudWhite
            )

            // 4. Sección Descripción
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Descripción:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = CloudWhite
                )
                Text(
                    text = if (currentProduct.descripcion.isNotBlank()) currentProduct.descripcion else "Sin descripción especificada.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CloudWhite.copy(alpha = 0.9f)
                )
            }

            // 5. Sección Materiales
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Materiales:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = CloudWhite
                )
                Text(
                    text = if (currentProduct.materiales.isNotBlank()) currentProduct.materiales else "Sin materiales especificados.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CloudWhite.copy(alpha = 0.9f)
                )
            }

            // 6. Sección Precio
            Text(
                text = "Precio: \$${currentProduct.precio}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Teal
            )

            statusMessage?.let { msg ->
                Text(
                    text = msg,
                    color = Teal,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 7. Botón "Agregar al Carrito"
            Button(
                onClick = {
                    isAddingToCart = true
                    statusMessage = null
                    coroutineScope.launch {
                        val result = FirestoreService.addToCart(
                            product = currentProduct,
                            userId = com.ucompensar.platinum.data.session.UserSession.currentUserId
                        )
                        isAddingToCart = false
                        result.fold(
                            onSuccess = {
                                statusMessage = "¡Producto agregado al carrito!"
                                loadCartCount()
                            },
                            onFailure = { ex ->
                                statusMessage = ex.localizedMessage ?: "Error al agregar al carrito"
                            }
                        )
                    }
                },
                enabled = !isAddingToCart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isAddingToCart) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Agregar al carrito",
                            tint = Color.Black
                        )
                        Text(
                            text = "Agregar al Carrito",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    PlatinumTheme {
        ProductDetailScreen()
    }
}
