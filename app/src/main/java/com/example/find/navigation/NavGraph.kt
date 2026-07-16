package com.example.find.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.find.ui.auth.AuthViewModel
import com.example.find.ui.auth.LoginScreen
import com.example.find.ui.auth.RegisterScreen
import com.example.find.ui.bench.AdminScreen
import com.example.find.ui.bench.BenchDetailScreen
import com.example.find.ui.bench.BenchViewModel
import com.example.find.ui.bench.ReportBenchScreen
import com.example.find.ui.bench.SearchScreen
import com.example.find.ui.map.LocationViewModel
import com.example.find.ui.map.MapScreen

@Composable
fun FindNavGraph(
    authViewModel: AuthViewModel,
    benchViewModel: BenchViewModel,
    locationViewModel: LocationViewModel
) {
    val navController = rememberNavController()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val session = authState.session

    NavHost(navController = navController, startDestination = Routes.MAP) {
        composable(Routes.MAP) {
            MapScreen(
                benchViewModel = benchViewModel,
                locationViewModel = locationViewModel,
                authViewModel = authViewModel,
                session = session,
                onBenchClick = { id -> navController.navigate(Routes.benchDetail(id)) },
                onNavigateToSearch = { navController.navigate(Routes.SEARCH) },
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                onNavigateToReport = { navController.navigate(Routes.REPORT) },
                onNavigateToAdmin = { navController.navigate(Routes.ADMIN) },
                onLogout = { authViewModel.logout() }
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                benchViewModel = benchViewModel,
                onBenchClick = { id -> navController.navigate(Routes.benchDetail(id)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { navController.popBackStack() },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.REPORT) {
            ReportBenchScreen(
                benchViewModel = benchViewModel,
                locationViewModel = locationViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN) {
            AdminScreen(
                benchViewModel = benchViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.BENCH_DETAIL,
            arguments = listOf(navArgument("benchId") { type = NavType.LongType })
        ) { backStackEntry ->
            val benchId = backStackEntry.arguments?.getLong("benchId") ?: return@composable
            BenchDetailScreen(
                benchId = benchId,
                benchViewModel = benchViewModel,
                session = session,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
