package com.example.safeair
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.safeair.api.ApiServices
import com.example.safeair.api.RetrofitInstance
import com.example.safeair.repository.TokenManager
import com.example.safeair.ui.theme.navigation.AppNavigation
import com.example.safeair.ui.theme.SafeAirTheme
import com.example.safeair.ui.theme.viewmodel.LoginViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager
    private lateinit var authService: ApiServices
    private lateinit var loginViewModelFactory: LoginViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {

        tokenManager = TokenManager.getInstance(applicationContext)


        val retrofit = RetrofitInstance.createRetrofit(tokenManager)


        authService = RetrofitInstance.createApiService(retrofit)

        // 4. Создание фабрики ViewModel
        loginViewModelFactory = LoginViewModelFactory(
            authService = authService,
            tokenManager = tokenManager
        )
        super.onCreate(savedInstanceState)
        setContent {
            SafeAirTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(loginViewModelFactory = loginViewModelFactory)
                }
            }
        }
    }
}