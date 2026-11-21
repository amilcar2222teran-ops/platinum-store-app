package com.ucompensar.platinum.data.service

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ucompensar.platinum.data.model.CartItemData
import com.ucompensar.platinum.data.model.OrderData
import com.ucompensar.platinum.data.model.ProductData
import com.ucompensar.platinum.data.model.UserData
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import java.util.UUID
import kotlin.coroutines.resume

object FirestoreService {
    private val db: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    private fun parseDouble(doc: DocumentSnapshot, field: String, default: Double = 0.0): Double {
        val raw = doc.get(field) ?: return default
        return when (raw) {
            is Number -> raw.toDouble()
            is String -> raw.toDoubleOrNull() ?: default
            else -> default
        }
    }

    private fun parseInt(doc: DocumentSnapshot, field: String, default: Int = 0): Int {
        val raw = doc.get(field) ?: return default
        return when (raw) {
            is Number -> raw.toInt()
            is String -> raw.toIntOrNull() ?: default
            else -> default
        }
    }

    /**
     * Autentica a un usuario buscando en la colección "usuarios" de Firestore por correo y contraseña.
     * Retorna el rol del usuario (1 = Admin, 2 = Vendedor, 3 = Usuario).
     */
    suspend fun authenticateUser(
        email: String,
        password: String
    ): Result<Int> = suspendCancellableCoroutine { continuation ->
        db.collection("usuarios")
            .whereEqualTo("correo", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.firstOrNull { doc ->
                        val pwd = doc.getString("contrasena") ?: doc.getString("password") ?: ""
                        pwd == password
                    }
                    if (document != null) {
                        val rol = parseInt(document, "rol", 3)
                        continuation.resume(Result.success(rol))
                    } else {
                        continuation.resume(Result.failure(Exception("Contraseña incorrecta")))
                    }
                } else {
                    continuation.resume(Result.failure(Exception("Correo no encontrado")))
                }
            }
            .addOnFailureListener { exception ->
                val errorMsg = if (exception.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                    "Permiso denegado (PERMISSION_DENIED): Revisa las reglas de Firestore en la consola de Firebase."
                } else {
                    "Error al consultar Firestore: ${exception.localizedMessage}"
                }
                continuation.resume(Result.failure(Exception(errorMsg)))
            }
    }

    /**
     * Obtiene la lista completa de todos los usuarios registrados en la colección "usuarios".
     */
    suspend fun getUsers(): Result<List<UserData>> = suspendCancellableCoroutine { continuation ->
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val users = querySnapshot.documents.map { doc ->
                    UserData(
                        id = doc.getString("id") ?: doc.getString("uuid") ?: doc.id,
                        correo = doc.getString("correo") ?: "",
                        contrasena = doc.getString("contrasena") ?: doc.getString("password") ?: "",
                        rol = parseInt(doc, "rol", 3)
                    )
                }
                continuation.resume(Result.success(users))
            }
            .addOnFailureListener { exception ->
                val errorMsg = if (exception.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                    "Permiso denegado (PERMISSION_DENIED): Revisa las reglas de Firestore en la consola de Firebase."
                } else {
                    "Error al obtener usuarios: ${exception.localizedMessage}"
                }
                continuation.resume(Result.failure(Exception(errorMsg)))
            }
    }

    /**
     * Guarda o actualiza un usuario en la colección "usuarios" de Firestore.
     * Si no posee ID, genera un UUID único para el nuevo documento.
     */
    suspend fun saveOrUpdateUser(
        user: UserData
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val docId = if (user.id.isNotBlank()) user.id else UUID.randomUUID().toString()

        val userMap = hashMapOf(
            "id" to docId,
            "uuid" to docId,
            "correo" to user.correo,
            "contrasena" to user.contrasena,
            "password" to user.contrasena,
            "rol" to user.rol
        )

        db.collection("usuarios")
            .document(docId)
            .set(userMap)
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                val errorMsg = if (exception.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                    "Permiso denegado (PERMISSION_DENIED): Revisa las reglas de Firestore en la consola de Firebase."
                } else {
                    "Error al guardar usuario: ${exception.localizedMessage}"
                }
                continuation.resume(Result.failure(Exception(errorMsg)))
            }
    }

    suspend fun saveUser(
        email: String,
        password: String,
        role: Int = 3
    ): Result<Unit> = saveOrUpdateUser(
        UserData(
            id = UUID.randomUUID().toString(),
            correo = email,
            contrasena = password,
            rol = role
        )
    )

    /**
     * Elimina un usuario de la colección "usuarios" de Firestore por su ID.
     */
    suspend fun deleteUser(userId: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        db.collection("usuarios")
            .document(userId)
            .delete()
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(Exception("Error al eliminar usuario: ${exception.localizedMessage}")))
            }
    }

    /**
     * Obtiene la lista completa de productos desde la colección "productos".
     */
    suspend fun getProducts(): Result<List<ProductData>> = suspendCancellableCoroutine { continuation ->
        db.collection("productos")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val products = querySnapshot.documents.map { doc ->
                    ProductData(
                        id = doc.getString("id") ?: doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        tag = doc.getString("tag") ?: "",
                        sizes = doc.getString("sizes") ?: "",
                        descripcion = doc.getString("descripcion") ?: "",
                        materiales = doc.getString("materiales") ?: "",
                        precio = parseDouble(doc, "precio", 0.0),
                        cantidad = parseInt(doc, "cantidad", 0),
                        imagenUrl = doc.getString("imagenUrl") ?: doc.getString("imagen") ?: ""
                    )
                }
                continuation.resume(Result.success(products))
            }
            .addOnFailureListener { exception ->
                val errorMsg = if (exception.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                    "Permiso denegado (PERMISSION_DENIED): Revisa las reglas de Firestore en la consola de Firebase."
                } else {
                    "Error al obtener productos: ${exception.localizedMessage}"
                }
                continuation.resume(Result.failure(Exception(errorMsg)))
            }
    }

    /**
     * Guarda o actualiza un producto en la colección "productos" de Firestore.
     * Si se selecciona/toma una nueva imagen, se sube a Firebase Storage.
     */
    suspend fun saveOrUpdateProduct(
        product: ProductData,
        imageBitmap: Bitmap? = null,
        imageUri: Uri? = null
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val productId = if (product.id.isNotBlank()) product.id else UUID.randomUUID().toString()

        fun uploadProductData(imageUrl: String) {
            val productMap = hashMapOf(
                "id" to productId,
                "nombre" to product.nombre,
                "tag" to product.tag,
                "sizes" to product.sizes,
                "descripcion" to product.descripcion,
                "materiales" to product.materiales,
                "precio" to product.precio,
                "cantidad" to product.cantidad,
                "imagenUrl" to imageUrl,
                "imagen" to imageUrl
            )

            db.collection("productos")
                .document(productId)
                .set(productMap)
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    val errorMsg = if (exception.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                        "Permiso denegado (PERMISSION_DENIED): Revisa las reglas de Firestore en la consola de Firebase."
                    } else {
                        "Error al guardar producto: ${exception.localizedMessage}"
                    }
                    continuation.resume(Result.failure(Exception(errorMsg)))
                }
        }

        if (imageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("productos/$productId.jpg")
            storageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl
                        .addOnSuccessListener { uri ->
                            uploadProductData(uri.toString())
                        }
                        .addOnFailureListener { ex ->
                            val msg = if (ex.message?.contains("object does not exist", ignoreCase = true) == true ||
                                ex.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                                "Error en Firebase Storage: Revisa las Reglas de Seguridad (Rules) en la Consola de Firebase para permitir 'read' y 'write'."
                            } else {
                                "Error al obtener URL de la imagen: ${ex.localizedMessage}"
                            }
                            continuation.resume(Result.failure(Exception(msg)))
                        }
                }
                .addOnFailureListener { ex ->
                    val errorMsg = if (ex.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                        "Permiso denegado en Firebase Storage: Revisa las Reglas en la consola de Firebase."
                    } else {
                        "Error al subir imagen a Firebase Storage: ${ex.localizedMessage}"
                    }
                    continuation.resume(Result.failure(Exception(errorMsg)))
                }
        } else if (imageBitmap != null) {
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
            val data = baos.toByteArray()

            val storageRef = FirebaseStorage.getInstance().reference.child("productos/$productId.jpg")
            storageRef.putBytes(data)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl
                        .addOnSuccessListener { uri ->
                            uploadProductData(uri.toString())
                        }
                        .addOnFailureListener { ex ->
                            val msg = if (ex.message?.contains("object does not exist", ignoreCase = true) == true ||
                                ex.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                                "Error en Firebase Storage: Revisa las Reglas de Seguridad (Rules) en la Consola de Firebase para permitir 'read' y 'write'."
                            } else {
                                "Error al obtener URL de la imagen: ${ex.localizedMessage}"
                            }
                            continuation.resume(Result.failure(Exception(msg)))
                        }
                }
                .addOnFailureListener { ex ->
                    val errorMsg = if (ex.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                        "Permiso denegado en Firebase Storage: Revisa las Reglas en la consola de Firebase."
                    } else {
                        "Error al subir imagen a Firebase Storage: ${ex.localizedMessage}"
                    }
                    continuation.resume(Result.failure(Exception(errorMsg)))
                }
        } else {
            uploadProductData(product.imagenUrl)
        }
    }

    /**
     * Elimina un producto de la colección "productos" de Firestore por su ID.
     */
    suspend fun deleteProduct(productId: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        db.collection("productos")
            .document(productId)
            .delete()
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(Exception("Error al eliminar producto: ${exception.localizedMessage}")))
            }
    }

    /**
     * Agrega o actualiza un producto en la colección "carrito" de Firestore para un usuario específico.
     */
    suspend fun addToCart(
        product: ProductData,
        userId: String = "usuario_default",
        quantity: Int = 1
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val safeUserId = if (userId.isNotBlank()) userId else "usuario_default"
        val cartItemId = "${safeUserId}_${product.id}"

        val cartMap = hashMapOf(
            "id" to cartItemId,
            "usuarioId" to safeUserId,
            "productoId" to product.id,
            "nombre" to product.nombre,
            "precio" to product.precio,
            "cantidad" to quantity,
            "imagenUrl" to product.imagenUrl,
            "fecha" to System.currentTimeMillis()
        )

        db.collection("carrito")
            .document(cartItemId)
            .set(cartMap)
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                val errorMsg = if (exception.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                    "Permiso denegado (PERMISSION_DENIED): Revisa las reglas de Firestore en la consola de Firebase."
                } else {
                    "Error al agregar al carrito: ${exception.localizedMessage}"
                }
                continuation.resume(Result.failure(Exception(errorMsg)))
            }
    }

    /**
     * Obtiene los elementos del carrito correspondientes a un usuario específico.
     */
    suspend fun getCartItems(userId: String = ""): Result<List<CartItemData>> = suspendCancellableCoroutine { continuation ->
        db.collection("carrito")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val items = querySnapshot.documents
                    .map { doc ->
                        CartItemData(
                            id = doc.getString("id") ?: doc.id,
                            usuarioId = doc.getString("usuarioId") ?: "",
                            productoId = doc.getString("productoId") ?: "",
                            nombre = doc.getString("nombre") ?: "",
                            precio = parseDouble(doc, "precio", 0.0),
                            cantidad = parseInt(doc, "cantidad", 1),
                            imagenUrl = doc.getString("imagenUrl") ?: "",
                            fecha = doc.getLong("fecha") ?: 0L
                        )
                    }
                    .filter { item ->
                        userId.isBlank() || item.usuarioId.isBlank() || item.usuarioId.equals(userId, ignoreCase = true)
                    }
                continuation.resume(Result.success(items))
            }
            .addOnFailureListener { exception ->
                val errorMsg = if (exception.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                    "Permiso denegado (PERMISSION_DENIED): Revisa las reglas de Firestore en la consola de Firebase."
                } else {
                    "Error al obtener carrito: ${exception.localizedMessage}"
                }
                continuation.resume(Result.failure(Exception(errorMsg)))
            }
    }

    /**
     * Elimina un producto de la colección "carrito" por su ID.
     */
    suspend fun deleteCartItem(cartItemId: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        db.collection("carrito")
            .document(cartItemId)
            .delete()
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(Exception("Error al eliminar del carrito: ${exception.localizedMessage}")))
            }
    }

    /**
     * Confirma el pedido guardando los items del carrito, la dirección de entrega,
     * las coordenadas GPS y la fecha en la colección "pedidos".
     */
    suspend fun confirmOrder(
        cartItems: List<CartItemData>,
        deliveryAddress: String,
        latitude: Double,
        longitude: Double,
        totalAmount: Double
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val orderId = UUID.randomUUID().toString()

        val itemsMap = cartItems.map { item ->
            mapOf(
                "productoId" to item.productoId,
                "nombre" to item.nombre,
                "precio" to item.precio,
                "cantidad" to item.cantidad
            )
        }

        val orderMap = hashMapOf(
            "id" to orderId,
            "items" to itemsMap,
            "direccionEntrega" to deliveryAddress,
            "latitud" to latitude,
            "longitud" to longitude,
            "total" to totalAmount,
            "fecha" to System.currentTimeMillis()
        )

        db.collection("pedidos")
            .document(orderId)
            .set(orderMap)
            .addOnSuccessListener {
                clearCart()
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                val errorMsg = if (exception.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                    "Permiso denegado (PERMISSION_DENIED): Revisa las reglas de Firestore en la consola de Firebase."
                } else {
                    "Error al confirmar pedido: ${exception.localizedMessage}"
                }
                continuation.resume(Result.failure(Exception(errorMsg)))
            }
    }

    /**
     * Obtiene la lista completa de todos los pedidos registrados en la colección "pedidos".
     */
    suspend fun getOrders(): Result<List<OrderData>> = suspendCancellableCoroutine { continuation ->
        db.collection("pedidos")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val orders = querySnapshot.documents.map { doc ->
                    @Suppress("UNCHECKED_CAST")
                    val itemsRaw = doc.get("items") as? List<Map<String, Any>> ?: emptyList()
                    val cartItems = itemsRaw.map { itemMap ->
                        CartItemData(
                            productoId = itemMap["productoId"]?.toString() ?: "",
                            nombre = itemMap["nombre"]?.toString() ?: "",
                            precio = (itemMap["precio"] as? Number)?.toDouble() ?: 0.0,
                            cantidad = (itemMap["cantidad"] as? Number)?.toInt() ?: 1
                        )
                    }
                    OrderData(
                        id = doc.getString("id") ?: doc.id,
                        items = cartItems,
                        direccionEntrega = doc.getString("direccionEntrega") ?: "",
                        latitud = parseDouble(doc, "latitud", 0.0),
                        longitud = parseDouble(doc, "longitud", 0.0),
                        total = parseDouble(doc, "total", 0.0),
                        fecha = doc.getLong("fecha") ?: 0L
                    )
                }
                continuation.resume(Result.success(orders))
            }
            .addOnFailureListener { exception ->
                val errorMsg = if (exception.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                    "Permiso denegado (PERMISSION_DENIED): Revisa las reglas de Firestore en la consola de Firebase."
                } else {
                    "Error al obtener pedidos: ${exception.localizedMessage}"
                }
                continuation.resume(Result.failure(Exception(errorMsg)))
            }
    }

    /**
     * Limpia todos los elementos de la colección "carrito".
     */
    fun clearCart() {
        db.collection("carrito").get().addOnSuccessListener { snapshot ->
            for (doc in snapshot.documents) {
                doc.reference.delete()
            }
        }
    }
}
