package com.ucompensar.platinum.ui.screens.users

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ucompensar.platinum.data.model.CartItemData
import com.ucompensar.platinum.data.service.FirestoreService
import com.ucompensar.platinum.ui.theme.CloudWhite
import com.ucompensar.platinum.ui.theme.PlatinumTheme
import com.ucompensar.platinum.ui.theme.Purple
import com.ucompensar.platinum.ui.theme.Teal
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    var cartItems by remember { mutableStateOf<List<CartItemData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessStatus by remember { mutableStateOf(false) }

    var deliveryAddress by remember { mutableStateOf("") }
    var userLatitude by remember { mutableDoubleStateOf(0.0) }
    var userLongitude by remember { mutableDoubleStateOf(0.0) }
    var isFetchingLocation by remember { mutableStateOf(false) }
    var isSubmittingOrder by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    fun loadCart() {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            val result = FirestoreService.getCartItems(com.ucompensar.platinum.data.session.UserSession.currentUserId)
            isLoading = false
            result.fold(
                onSuccess = { items ->
                    cartItems = items
                },
                onFailure = { ex ->
                    errorMessage = ex.localizedMessage ?: "Error al cargar el carrito"
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        loadCart()
    }

    val totalAmount = remember(cartItems) {
        cartItems.sumOf { it.precio * it.cantidad }
    }

    // Función auxiliar para solicitar ubicación GPS
    fun obtainGpsLocation() {
        isFetchingLocation = true
        statusMessage = null
        getCurrentUserLocation(
            context = context,
            onLocationObtained = { address, lat, lng ->
                isFetchingLocation = false
                deliveryAddress = address
                userLatitude = lat
                userLongitude = lng
                isSuccessStatus = true
                statusMessage = "¡Ubicación GPS capturada exitosamente!"
            },
            onError = { error ->
                isFetchingLocation = false
                isSuccessStatus = false
                statusMessage = error
            }
        )
    }

    fun requestLocationAndFillAddress() {
        val fineLocationCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineLocationCheck == PackageManager.PERMISSION_GRANTED || coarseLocationCheck == PackageManager.PERMISSION_GRANTED) {
            obtainGpsLocation()
        } else {
            val activity = context as? android.app.Activity
            if (activity != null) {
                androidx.core.app.ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1001
                )
            } else {
                obtainGpsLocation()
            }
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Carrito de Compras",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = CloudWhite
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Lista de productos del carrito
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = 280.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CloudWhite.copy(alpha = 0.1f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Teal)
                    } else if (errorMessage != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { loadCart() },
                                colors = ButtonDefaults.buttonColors(containerColor = Teal)
                            ) {
                                Text("Reintentar", color = CloudWhite)
                            }
                        }
                    } else if (cartItems.isEmpty()) {
                        Text(
                            text = "El carrito está vacío",
                            color = CloudWhite.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(cartItems) { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = CloudWhite.copy(alpha = 0.15f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.nombre,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = CloudWhite,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Precio: \$${item.precio} | Cant: ${item.cantidad}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = CloudWhite.copy(alpha = 0.8f)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    FirestoreService.deleteCartItem(item.id)
                                                    loadCart()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar del carrito",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Sección de Dirección de Entrega y Geolocalización GPS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CloudWhite.copy(alpha = 0.15f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Dirección de Entrega",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CloudWhite
                    )

                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = {
                            deliveryAddress = it
                            statusMessage = null
                        },
                        label = { Text("Escribe o captura tu dirección") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Dirección"
                            )
                        },
                        enabled = !isSubmittingOrder && !isFetchingLocation,
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

                    // Botón para capturar la ubicación por GPS
                    OutlinedButton(
                        onClick = { requestLocationAndFillAddress() },
                        enabled = !isSubmittingOrder && !isFetchingLocation,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Teal),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Teal)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isFetchingLocation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Teal,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Obteniendo ubicación GPS...")
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "GPS Location",
                                    tint = Teal
                                )
                                Text(
                                    text = "Usar Mi Ubicación Actual (GPS)",
                                    fontWeight = FontWeight.Bold,
                                    color = Teal
                                )
                            }
                        }
                    }

                    if (userLatitude != 0.0 || userLongitude != 0.0) {
                        Text(
                            text = "Coordenadas capturadas: Lat %.4f, Lng %.4f".format(userLatitude, userLongitude),
                            style = MaterialTheme.typography.labelSmall,
                            color = CloudWhite.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Resumen de Total
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CloudWhite.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total a Pagar:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CloudWhite
                    )
                    Text(
                        text = "\$$totalAmount",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Teal
                    )
                }
            }

            // Mensaje de estado (éxito o error)
            statusMessage?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = msg,
                    color = if (isSuccessStatus) Teal else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Botón "Confirmar Pedido"
            Button(
                onClick = {
                    if (cartItems.isEmpty()) {
                        statusMessage = "El carrito está vacío. Agrega productos primero."
                        isSuccessStatus = false
                        return@Button
                    }
                    if (deliveryAddress.isBlank()) {
                        statusMessage = "Por favor ingresa una dirección de entrega o usa tu ubicación GPS."
                        isSuccessStatus = false
                        return@Button
                    }

                    isSubmittingOrder = true
                    statusMessage = null

                    coroutineScope.launch {
                        val result = FirestoreService.confirmOrder(
                            cartItems = cartItems,
                            deliveryAddress = deliveryAddress,
                            latitude = userLatitude,
                            longitude = userLongitude,
                            totalAmount = totalAmount
                        )
                        isSubmittingOrder = false
                        result.fold(
                            onSuccess = {
                                isSuccessStatus = true
                                statusMessage = "¡Pedido confirmado exitosamente! Tu orden fue registrada."
                                deliveryAddress = ""
                                userLatitude = 0.0
                                userLongitude = 0.0
                                loadCart()
                            },
                            onFailure = { ex ->
                                isSuccessStatus = false
                                statusMessage = ex.localizedMessage ?: "Error al confirmar pedido"
                            }
                        )
                    }
                },
                enabled = !isSubmittingOrder && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal,
                    contentColor = androidx.compose.ui.graphics.Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isSubmittingOrder) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = androidx.compose.ui.graphics.Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Confirmar Pedido",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentUserLocation(
    context: Context,
    onLocationObtained: (String, Double, Double) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val fineCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineCheck != PackageManager.PERMISSION_GRANTED && coarseCheck != PackageManager.PERMISSION_GRANTED) {
            onError("No se otorgaron los permisos de ubicación GPS.")
            return
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            onError("El servicio de ubicación no está disponible en este dispositivo.")
            return
        }

        val isGpsEnabled = try { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } catch (e: Exception) { false }
        val isNetworkEnabled = try { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } catch (e: Exception) { false }

        if (!isGpsEnabled && !isNetworkEnabled) {
            onError("El GPS y la ubicación están desactivados en tu dispositivo. Por favor actívalos.")
            return
        }

        var locationFound = false

        val providersToTry = listOfNotNull(
            if (isNetworkEnabled) LocationManager.NETWORK_PROVIDER else null,
            if (isGpsEnabled) LocationManager.GPS_PROVIDER else null,
            LocationManager.PASSIVE_PROVIDER
        )

        for (p in providersToTry) {
            try {
                val loc = locationManager.getLastKnownLocation(p)
                if (loc != null) {
                    val lat = loc.latitude
                    val lng = loc.longitude
                    val address = getAddressFromCoordinates(context, lat, lng)
                    onLocationObtained(address, lat, lng)
                    locationFound = true
                    break
                }
            } catch (e: Exception) {
                // Continuar intentando con el siguiente proveedor
            }
        }

        if (!locationFound) {
            val provider = if (isNetworkEnabled) LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER
            val mainLooper = context.mainLooper ?: android.os.Looper.getMainLooper()

            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    try {
                        locationManager.removeUpdates(this)
                    } catch (e: Exception) {}
                    val lat = location.latitude
                    val lng = location.longitude
                    val address = getAddressFromCoordinates(context, lat, lng)
                    onLocationObtained(address, lat, lng)
                }
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
            }

            locationManager.requestLocationUpdates(
                provider,
                0L,
                0f,
                locationListener,
                mainLooper
            )
        }
    } catch (e: Exception) {
        onError("No se pudo obtener la ubicación GPS: ${e.localizedMessage ?: "Error desconocido"}")
    }
}

private fun getAddressFromCoordinates(context: Context, lat: Double, lng: Double): String {
    return try {
        if (!Geocoder.isPresent()) {
            return "Lat: %.4f, Lng: %.4f".format(Locale.US, lat, lng)
        }
        val geocoder = Geocoder(context, Locale.getDefault())
        @Suppress("DEPRECATION")
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        if (!addresses.isNullOrEmpty()) {
            val addr = addresses[0]
            val line = addr.getAddressLine(0)
            if (!line.isNullOrBlank()) line else "Lat: %.4f, Lng: %.4f".format(Locale.US, lat, lng)
        } else {
            "Lat: %.4f, Lng: %.4f".format(Locale.US, lat, lng)
        }
    } catch (e: Exception) {
        "Lat: %.4f, Lng: %.4f".format(Locale.US, lat, lng)
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    PlatinumTheme {
        CartScreen()
    }
}
