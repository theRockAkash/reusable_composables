package com.solutions.billnest.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.solutions.billnest.R
import com.solutions.billnest.ui.composables.BackButton
import com.solutions.billnest.ui.composables.LottieView
import com.solutions.billnest.ui.composables.OutlineButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class LocationHelper(
    private val context: Context,
) {
    companion object {
        var currentLocation by mutableStateOf<Location?>(null)
    }

    private var resolutionLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>? = null
    private var permissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>? = null
    private var onLocationResult: ((Location) -> Unit)? = null

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun setResolutionLauncher(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) {
        resolutionLauncher = launcher
    }

    fun setPermissionLauncher(launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>) {
        permissionLauncher = launcher
    }

    private val locationRequest by lazy {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(30)
            .setMaxUpdateDelayMillis(2)
            .build()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun getCurrentLocation(onLocationResult: ((Location) -> Unit)? = null) {
        if (currentLocation != null) {
            onLocationResult?.invoke(currentLocation!!)
            return
        }
        onLocationResult?.let { this.onLocationResult = it }

        val hasFine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            permissionLauncher?.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        _isLoading.value = true
        if (isLocationEnabled()) {
            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                _isLoading.value = false
                currentLocation = location
                onLocationResult?.invoke(location)
            }.addOnFailureListener {
                getLastKnownLocation()
            }
        } else {
            _isLoading.value = false
            promptEnableLocation()
        }
    }

    private fun promptEnableLocation() {
        val builder = LocationSettingsRequest
            .Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()
        LocationServices
            .getSettingsClient(context)
            .checkLocationSettings(builder)
            .addOnFailureListener { ex ->
                if (ex is ResolvableApiException && context.activity() != null && resolutionLauncher != null) {
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(ex.resolution).build()
                        resolutionLauncher?.launch(intentSenderRequest)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        sendEx.printStackTrace()
                    }
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        _isLoading.value = true
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                _isLoading.value = false
                currentLocation = location
                onLocationResult?.invoke(location)
            }.addOnFailureListener {
                _isLoading.value = false
            }
    }
}

@Composable
fun rememberLocationHelper(key: Any? = null, onLocationResult: ((Location) -> Unit)? = null): LocationHelper {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    // Setup launchers only once
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.any { it.value }) {
            locationHelper.getCurrentLocation()
        } else {
            context.showToast("Location Permission denied")
        }
    }
    val resolutionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            locationHelper.getCurrentLocation()
        } else {
            context.showToast("Location not enabled")
        }
    }
    LaunchedEffect(Unit) {
        locationHelper.setResolutionLauncher(resolutionLauncher)
        locationHelper.setPermissionLauncher(permissionLauncher)
        if (onLocationResult != null)
            locationHelper.getCurrentLocation()
    }
    if (onLocationResult != null)
        LaunchedEffect(key) {
            LocationHelper.currentLocation?.let { onLocationResult.invoke(it) }
        }
    return locationHelper
}

@Preview(showBackground = true)
@Composable
fun LocationWrapper(modifier: Modifier = Modifier, content: @Composable (Location) -> Unit = {}) {
    val locationHelper = rememberLocationHelper()
    val isLoading by locationHelper.isLoading.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        locationHelper.getCurrentLocation()
    }
    LocationHelper.currentLocation?.let {
        content.invoke(it)
    } ?: run {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            LottieView(res = R.raw.location, modifier = Modifier.height(250.dp), color = MaterialTheme.colorScheme.primary)
            Text("Waiting for location...", modifier = Modifier.padding(bottom = 16.dp))
            OutlineButton(text = "  Retry  ", loading = isLoading, enabled = isLoading.not()) {
                locationHelper.getCurrentLocation()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScaffold(
    label: String,
    onBackPressed: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (Location, PaddingValues) -> Unit
) {
    val locationHelper = rememberLocationHelper()
    val isLoading by locationHelper.isLoading.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        locationHelper.getCurrentLocation()
    }
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = label
                )
            },
            navigationIcon = {
                if (onBackPressed != null)
                    BackButton(onBackPressed = onBackPressed)
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }) { paddingValues ->
        LocationHelper.currentLocation?.let {
            content.invoke(it, paddingValues)
        } ?: run {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LottieView(
                    res = R.raw.location, modifier = Modifier.height(250.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Waiting for location...", modifier = Modifier.padding(bottom = 24.dp))
                OutlineButton(
                    text = "Retry",
                    loading = isLoading,
                    enabled = isLoading.not(),
                    modifier = Modifier.fillMaxWidth(.4f)
                ) {
                    locationHelper.getCurrentLocation()
                }
            }

        }

    }

}