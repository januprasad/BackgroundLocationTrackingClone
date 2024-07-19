package com.github.januprasad.backgroundlocationtracking

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
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
        val cameraPositionState = rememberCameraPositionState()
        cameraPositionState.position.target
        val zoomLevel = 1.0f
        LaunchedEffect(true) {
            viewModel.startLocationUpdates(c)
            viewModel.mutableSharedFlow.collectLatest { data ->
                state.value = data
                val newPosition =
                    CameraPosition.fromLatLngZoom(
                        LatLng(
                            data.first.toDouble(),
                            data.second.toDouble(),
                        ),
                        zoomLevel,
                    )
                cameraPositionState.position = newPosition
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

            GoogleMap(modifier = Modifier.fillMaxWidth(), cameraPositionState = cameraPositionState) {
            }
        }
    }
}
