package com.github.januprasad.backgroundlocationtracking

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.januprasad.backgroundlocationtracking.ui.theme.BackgroundLocationTrackingTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0,
        )
        setContent {
            BackgroundLocationTrackingTheme {
                MainApp()
            }
        }
    }

    @Composable
    private fun MainApp(viewModel: LocationViewModel = viewModel()) {
        val c = LocalContext.current
        val state =
            remember {
                mutableStateOf(Pair("Fetching", "Fetching"))
            }
        LaunchedEffect(true) {
            viewModel.startLocationUpdates(c)
            viewModel.mutableSharedFlow.collectLatest { data ->
                state.value = data
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(text = "Lat ${state.value.first}")
            Text(text = "Long ${state.value.second}")

            Button(onClick = {
                Intent(applicationContext, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                    startService(this)
                }
            }) {
                Text(text = "Start")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                Intent(applicationContext, LocationService::class.java).apply {
                    action = LocationService.ACTION_STOP
                    startService(this)
                }
            }) {
                Text(text = "Stop")
            }
        }
    }
}
