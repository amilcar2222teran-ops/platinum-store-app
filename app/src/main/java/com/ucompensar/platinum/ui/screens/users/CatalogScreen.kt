package com.ucompensar.platinum.ui.screens.users

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    modifier: Modifier = Modifier,
    onProductClick: (ProductData) -> Unit = {},
    onCartClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    var products by remember { mutableStateOf<List<ProductData>>(emptyList()) }
    var cartItemCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    fun loadData() {
        isLoading = true
        coroutineScope.launch {
            val productsResult = FirestoreService.getProducts()
            productsResult.onSuccess { list ->
                products = list
            }

            val cartResult = FirestoreService.getCartItems(com.ucompensar.platinum.data.session.UserSession.currentUserId)
            cartResult.onSuccess { items ->
                cartItemCount = items.sumOf { it.cantidad }
            }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    val categories = listOf("Anillo", "Collar", "Arete")

    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        products.filter { p ->
            val matchesQuery = searchQuery.isBlank() || p.nombre.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || p.tag.equals(selectedCategory, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Purple)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Fila Superior: Barra de Búsqueda, Carrito y Cerrar Sesión
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

                // Icono del Carrito con Contador (Badge)
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

                // Botón Cerrar Sesión
                IconButton(onClick = onLogoutClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint = CloudWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Categorías (Filtros: Anillo, Collar, Arete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                selectedCategory = if (isSelected) null else category
                            },
                        color = if (isSelected) Teal else Teal.copy(alpha = 0.85f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Grilla de Productos (2 columnas)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Teal)
                } else if (filteredProducts.isEmpty()) {
                    Text(
                        text = if (products.isEmpty()) "No hay productos disponibles" else "No se encontraron productos",
                        color = CloudWhite.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredProducts) { product ->
                            Card(
                                onClick = { onProductClick(product) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = CloudWhite
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = product.nombre,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            textAlign = TextAlign.Center
                                        )
                                        if (product.precio > 0) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "\$${product.precio}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogScreenPreview() {
    PlatinumTheme {
        CatalogScreen()
    }
}
