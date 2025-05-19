package com.solutions.billnest.ui.composables

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.solutions.billnest.util.createImageFile
import com.solutions.billnest.util.showToast
import kotlinx.coroutines.launch

/**
 * @Created by akash on 14-04-2025.
 * Know more about author at https://akash.cloudemy.in
 */
enum class ImagePickerType {
    CAMERA,
    GALLERY,
    CAMERA_GALLERY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePicker(
    pickerType: ImagePickerType = ImagePickerType.CAMERA_GALLERY,
    preview: Boolean = true,
    onImagePicked: (Uri) -> Unit,
    buttonContent: @Composable (trigger: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var showPreviewDialog by remember { mutableStateOf(false) }


    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            tempImageUri = it

            if (preview)
                showPreviewDialog = true
            else
                onImagePicked.invoke(it)
        }
    }


    // Camera Capture Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let {
                if (preview)
                    showPreviewDialog = true
                else
                    onImagePicked.invoke(it)
            }
        }
    }

    fun launchCamera() {
        FileProvider.getUriForFile(/* context = */ context,/* authority = */
            "${context.packageName}.provider",/* file = */
            context.createImageFile()
        )?.let {
            tempImageUri = it
            cameraLauncher.launch(it)
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchCamera()
        } else {
            context.showToast("Camera permission denied")
        }
    }

    fun launchPicker() {
        when (pickerType) {
            ImagePickerType.CAMERA -> {
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    launchCamera()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }

            ImagePickerType.GALLERY -> {
                galleryLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }

            ImagePickerType.CAMERA_GALLERY -> scope.launch { sheetState.show() }
        }

    }
    buttonContent.invoke(::launchPicker)
    if (sheetState.isVisible)
        ModalBottomSheet(onDismissRequest = { scope.launch { sheetState.hide() } },
            sheetState = sheetState,
            content = {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Choose Image From", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // if(pickerType==ImagePickerType.CAMERA||pickerType==ImagePickerType.CAMERA_GALLERY)
                        OutlineButton(
                            text = "Camera",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    launchCamera()
                                } else {
                                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }

                                scope.launch { sheetState.hide() }
                            })
                        //  if(pickerType==ImagePickerType.GALLERY||pickerType==ImagePickerType.CAMERA_GALLERY)
                        OutlineButton(
                            text = "Gallery",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                                scope.launch { sheetState.hide() }
                            })
                    }

                }
            })

    if (showPreviewDialog)
        ImagePreviewDialog(
            model = tempImageUri,
            onDismiss = { showPreviewDialog = false },
            positiveButtonText = "Done"
        ) {
            showPreviewDialog = false
            tempImageUri?.let { onImagePicked.invoke(it) }
        }
}


