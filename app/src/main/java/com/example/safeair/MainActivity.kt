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
import com.example.safeair.api.WeatherApiService
import com.example.safeair.repository.TokenManager
import com.example.safeair.ui.theme.navigation.AppNavigation
import com.example.safeair.ui.theme.SafeAirTheme
import com.example.safeair.ui.theme.viewmodel.HomeViewModelFactory
import com.example.safeair.ui.theme.viewmodel.LoginViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager
    private lateinit var authService: ApiServices
    private lateinit var weatherApiService: WeatherApiService
    private lateinit var loginViewModelFactory: LoginViewModelFactory
    private lateinit var homeViewModelFactory: HomeViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {

        tokenManager = TokenManager.getInstance(applicationContext)

        val authRetrofit = RetrofitInstance.createAuthRetrofit(tokenManager)
        authService = RetrofitInstance.createApiService(authRetrofit)

        val weatherRetrofit = RetrofitInstance.createWeatherRetrofit()
        weatherApiService = RetrofitInstance.createWeatherApiService(weatherRetrofit)

        loginViewModelFactory = LoginViewModelFactory(
            authService = authService,
            tokenManager = tokenManager
        )
        
        homeViewModelFactory = HomeViewModelFactory(
            weatherApiService = weatherApiService
        )
        
        super.onCreate(savedInstanceState)
        setContent {
            SafeAirTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        loginViewModelFactory = loginViewModelFactory,
                        homeViewModelFactory = homeViewModelFactory
                    )
                }
            }
        }
    }
}