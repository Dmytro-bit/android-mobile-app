@file:OptIn(ExperimentalFoundationApi::class) // <-- ADD THIS LINE

package com.example.safeair.ui.theme.screens

import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.core.FiniteAnimationSpec // Этот импорт может потребоваться
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.safeair.ui.theme.SafeAirTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// --- Data Model & ViewModel (Remain Unchanged) ---
data class AirQualityData(
    val id: String,
    val location: String,
    val aqi: Int,
    val countryFlagUrl: String
)

class HomeViewModel : ViewModel() {
    private val _airQualityData = MutableStateFlow<List<AirQualityData>>(emptyList())
    val airQualityData = _airQualityData.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Fetching initial data...")
            _isLoading.value = true
            delay(2000)
            _airQualityData.value = getFakeAirQualityData()
            _isLoading.value = false
            Log.d("HomeViewModel", "Data fetched successfully.")
        }
    }

    private fun getFakeAirQualityData(): List<AirQualityData> {
        return listOf(
            AirQualityData("1", "Los Angeles, USA", 155, "https://flagsapi.com/US/shiny/64.png"),
            AirQualityData("2", "New Delhi, IND", 250, "https://flagsapi.com/IN/shiny/64.png"),
            AirQualityData("3", "Beijing, CHN", 180, "https://flagsapi.com/CN/shiny/64.png"),
            AirQualityData("4", "London, GBR", 45, "https://flagsapi.com/GB/shiny/64.png"),
            AirQualityData("5", "Sydney, AUS", 30, "https://flagsapi.com/AU/shiny/64.png"),
            AirQualityData("6", "Tokyo, JPN", 55, "https://flagsapi.com/JP/shiny/64.png")
        )
    }
}

// --- 1. SMART COMPOSABLE (Entry Point) ---
@Composable
fun HomeScreenRoute(
    viewModel: HomeViewModel = viewModel()
) {
    // This composable holds the state and logic
    val airQualityList by viewModel.airQualityData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Pass the state and events down to the dumb UI composable
    HomeScreen(
        isLoading = isLoading,
        airQualityList = airQualityList,
        onCardClick = { location ->
            Log.d("HomeScreenRoute", "Card clicked for location: $location")
            // Handle navigation or other actions here
        }
    )
}

// --- 2. DUMB COMPOSABLE (Stateless UI) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isLoading: Boolean,
    airQualityList: List<AirQualityData>,
    onCardClick: (String) -> Unit, // Event passed as a lambda
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Safe Air") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AirQualityList(
                dataList = airQualityList,
                onCardClick = onCardClick, // Pass the event down
                modifier = Modifier.fillMaxSize()
            )

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LoadingIndicator()
            }
        }
    }
}

// Файл: HomeScreen.kt

@Composable
fun AirQualityList(
    dataList: List<AirQualityData>,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (dataList.isEmpty()) {
        // ... (empty state content)
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = dataList,
                key = { it.id }
            ) { data ->
                AirQualityCard(
                    data = data,
                    onClick = { onCardClick(data.location) },
                    // 🚨 УДАЛЯЕМ animateItemPlacement и tween
                    modifier = Modifier
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Needed for clickable Card in M3
@Composable
fun AirQualityCard(
    data: AirQualityData,
    onClick: () -> Unit, // Accept the event
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick, // Make the Card clickable
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        // ... (Row, Image, Column content remains the same)
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = data.countryFlagUrl),
                contentDescription = "${data.location} Flag",
                modifier = Modifier.size(50.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.location,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Air Quality Index: ${data.aqi}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    // ... (This function remains the same)
}

// --- 3. UPDATED PREVIEWS ---
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview_LoadedState() {
    SafeAirTheme {
        // Preview the dumb composable by passing fake data directly. No ViewModel!
        HomeScreen(
            isLoading = false,
            airQualityList = listOf(
                AirQualityData("1", "Los Angeles", 155, ""),
                AirQualityData("2", "New Delhi", 250, "")
            ),
            onCardClick = {} // Pass an empty lambda for the preview
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun HomeScreenPreview_LoadingState() {
    SafeAirTheme {
        HomeScreen(
            isLoading = true,
            airQualityList = emptyList(), // Pass an empty list for loading state
            onCardClick = {}
        )
    }
}