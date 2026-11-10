package com.ucompensar.platinum.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucompensar.platinum.ui.screens.LoginScreen
import com.ucompensar.platinum.ui.screens.RoleSelectionScreen
import com.ucompensar.platinum.ui.screens.administrator.DashboardScreen
import com.ucompensar.platinum.ui.screens.administrator.ProductAdministrationScreen
import com.ucompensar.platinum.ui.screens.administrator.UserAdministrationScreen

import com.ucompensar.platinum.ui.screens.seller.SellHistoryScreen
import com.ucompensar.platinum.ui.screens.users.CatalogScreen
import com.ucompensar.platinum.ui.screens.users.ProductDetailScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // 1. Login
        composable("login") {
            LoginScreen(
                onLoginClick = { _, _ ->
                    navController.navigate("role_selection")
                }
            )
        }

        // 2. Selección de Rol
        composable("role_selection") {
            RoleSelectionScreen(
                onAdminClick = {
                    navController.navigate("admin_dashboard")
                },
                onVendorClick = {
                    navController.navigate("seller_history")
                },
                onUserClick = {
                    navController.navigate("catalog")
                }
            )
        }

        // 3. Administrador
        composable("admin_dashboard") {
            DashboardScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onUserClick = {
                    navController.navigate("admin_user")
                },
                onProductClick = {
                    navController.navigate("admin_product")
                }
            )
        }
        // 3.1  Administrador Productos
        composable("admin_product") {
            ProductAdministrationScreen(

            )
        }
        // 3.2  Administrador Usuarios
        composable("admin_user"){
            UserAdministrationScreen(
            )
        }
        // 4. Vendedor - Historial de Ventas
        composable("seller_history") {
            SellHistoryScreen()
        }
        // 5. Catálogo de Usuarios
        composable("catalog") {
            CatalogScreen(
                onProductClick = { _ ->
                    navController.navigate("product_detail")
                }
            )
        }
        // 6. Detalle del Producto
        composable("product_detail") {
            ProductDetailScreen()
        }
    }
}
