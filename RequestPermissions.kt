package com.solutions.billnest.ui.composables

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.solutions.billnest.util.gotoApplicationSettings

/**
 * @Created by akash on 01-04-2025.
 * Know more about author at https://akash.cloudemy.in
 */
@Composable
fun RequestAnyPermissions(
    permissions: Array<String>,
    onGranted: () -> Unit,
    content: @Composable (() -> Unit) -> Unit
) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val isAnyGranted = permissionsMap.entries.any { it.value } // Check if any permission is granted
        if (isAnyGranted) {
            onGranted.invoke()
        } else {
            showPermissionDialog = true
        }
    }

    fun checkPermission() {
        val isAnyGranted = permissions.any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (isAnyGranted) {
            onGranted.invoke()
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    content.invoke(::checkPermission)

    if (showPermissionDialog)
        ShowPermissionDialog {
            showPermissionDialog = false
        }
}

@Composable
fun RequestAllPermissions(
    permissions: Array<String>,
    onGranted: () -> Unit,
    content: @Composable (() -> Unit) -> Unit
) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val areGranted = permissionsMap.values.reduceOrNull { acc, next -> acc && next }
        if (areGranted == true) {
            onGranted.invoke()
        } else {
            // Show dialog
            showPermissionDialog = true
        }
    }

    fun checkPermission() {
        if (!permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
            permissionLauncher.launch(permissions)

        } else {
            onGranted.invoke()
        }
    }
    content.invoke(::checkPermission)
    if (showPermissionDialog)
        ShowPermissionDialog {
            showPermissionDialog = false
        }
}


@Composable
fun ShowPermissionDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss.invoke()}) {
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
            Column(Modifier.background(MaterialTheme.colorScheme.errorContainer)) {
               /* LottieView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    res = R.raw.error
                )*/
                Text(
                    "Permissions Denied",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current
                Text(
                    "Required Permissions",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    "Camera, Microphone & Storage",
                    textAlign = TextAlign.Center,
                )
                Text(
                    "App Settings > Permissions > Click on each to grant permission",
                    textAlign = TextAlign.Center,
                    color =MaterialTheme.colorScheme.error
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlineButton(
                        "Cancel",
                        onClick = { onDismiss.invoke() },
                    )
                    RoundedButton("App Setting",onClick = {
                        onDismiss.invoke()
                        context.gotoApplicationSettings()
                    })
                }
            }

        }
    }
}