package com.ucompensar.platinum.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucompensar.platinum.data.model.ProductData
import com.ucompensar.platinum.data.model.UserData
import com.ucompensar.platinum.data.session.UserSession
import com.ucompensar.platinum.ui.screens.LoginScreen
import com.ucompensar.platinum.ui.screens.RegisterScreen
import com.ucompensar.platinum.ui.screens.administrator.DashboardScreen
import com.ucompensar.platinum.ui.screens.administrator.ProductAdministrationScreen
import com.ucompensar.platinum.ui.screens.administrator.ProductListScreen
import com.ucompensar.platinum.ui.screens.administrator.UserAdministrationScreen
import com.ucompensar.platinum.ui.screens.administrator.UserListScreen
import com.ucompensar.platinum.ui.screens.seller.SellHistoryScreen
import com.ucompensar.platinum.ui.screens.users.CartScreen
import com.ucompensar.platinum.ui.screens.users.CatalogScreen
import com.ucompensar.platinum.ui.screens.users.ProductDetailScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    var selectedCatalogProduct by remember { mutableStateOf<ProductData?>(null) }

    fun logout() {
        UserSession.currentUserEmail = ""
        UserSession.currentUserId = ""
        UserSession.currentUserRole = 3
        navController.navigate("login") {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // 1. Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = { role ->
                    when (role) {
                        1 -> navController.navigate("admin_dashboard")
                        2 -> navController.navigate("seller_history")
                        3 -> navController.navigate("catalog")
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        // 2. Registro de Usuario (Rol 3 por defecto)
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Administrador Dashboard
        composable("admin_dashboard") {
            DashboardScreen(
                onUserClick = {
                    navController.navigate("admin_user")
                },
                onProductClick = {
                    navController.navigate("admin_product")
                },
                onLogoutClick = {
                    logout()
                }
            )
        }

        // 3.1 Administrador Productos (Lista y Administración)
        composable("admin_product") {
            var productToEdit by remember { mutableStateOf<ProductData?>(null) }
            var isEditingProduct by remember { mutableStateOf(false) }

            if (isEditingProduct) {
                ProductAdministrationScreen(
                    productToEdit = productToEdit,
                    onSaveSuccess = {
                        isEditingProduct = false
                        productToEdit = null
                    }
                )
            } else {
                ProductListScreen(
                    onProductClick = { selectedProduct ->
                        productToEdit = selectedProduct
                        isEditingProduct = true
                    },
                    onCreateProductClick = {
                        productToEdit = null
                        isEditingProduct = true
                    },
                    onLogoutClick = {
                        logout()
                    }
                )
            }
        }

        // 3.2 Administrador Usuarios (Lista y Administración)
        composable("admin_user") {
            var userToEdit by remember { mutableStateOf<UserData?>(null) }
            var isEditingUser by remember { mutableStateOf(false) }

            if (isEditingUser) {
                UserAdministrationScreen(
                    userToEdit = userToEdit,
                    onSaveSuccess = {
                        isEditingUser = false
                        userToEdit = null
                    },
                    onBackClick = {
                        isEditingUser = false
                        userToEdit = null
                    }
                )
            } else {
                UserListScreen(
                    onUserClick = { selectedUser ->
                        userToEdit = selectedUser
                        isEditingUser = true
                    },
                    onCreateUserClick = {
                        userToEdit = null
                        isEditingUser = true
                    },
                    onLogoutClick = {
                        logout()
                    }
                )
            }
        }

        // 4. Vendedor - Historial de Ventas
        composable("seller_history") {
            SellHistoryScreen(
                onLogoutClick = {
                    logout()
                }
            )
        }

        // 5. Catálogo de Usuarios
        composable("catalog") {
            CatalogScreen(
                onProductClick = { selectedProduct ->
                    selectedCatalogProduct = selectedProduct
                    navController.navigate("product_detail")
                },
                onCartClick = {
                    navController.navigate("cart")
                },
                onLogoutClick = {
                    logout()
                }
            )
        }

        // 6. Detalle del Producto
        composable("product_detail") {
            ProductDetailScreen(
                product = selectedCatalogProduct,
                onCartClick = {
                    navController.navigate("cart")
                }
            )
        }

        // 7. Carrito de Compras
        composable("cart") {
            CartScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
