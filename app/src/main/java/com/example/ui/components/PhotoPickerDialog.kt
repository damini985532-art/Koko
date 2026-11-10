package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.ui.theme.CateringBorder
import com.example.ui.theme.CateringCardElevated
import com.example.ui.theme.CateringCardNavy
import com.example.ui.theme.CateringDangerRed
import com.example.ui.theme.CateringDarkNavy
import com.example.ui.theme.CateringOrange
import com.example.ui.theme.CateringPackedGreen
import com.example.ui.theme.CateringTextMuted
import com.example.ui.theme.CateringTextPrimary
import com.example.ui.theme.CateringTextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun PhotoPickerDialog(
    title: String = "फोटो जोड़ें / बदलें",
    currentPhotoUri: String = "",
    onDismiss: () -> Unit,
    onPhotoSelected: (permanentPath: String) -> Unit,
    onPhotoRemoved: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Preview state before final confirmation
    var previewUri by remember { mutableStateOf<Uri?>(null) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isPreviewActive by remember { mutableStateOf(false) }
    var previewSourceIsCamera by remember { mutableStateOf(false) }

    // Temp file for camera
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraFile by remember { mutableStateOf<File?>(null) }

    fun createTempCameraFile(): Pair<File, Uri> {
        val cacheDir = File(context.cacheDir, "catering_cache")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        val file = File(cacheDir, "camera_capture_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return Pair(file, uri)
    }

    // Camera take picture contract (full resolution via FileProvider)
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            previewUri = tempCameraUri
            previewBitmap = null
            previewSourceIsCamera = true
            isPreviewActive = true
            errorMessage = null
        }
    }

    // Fallback thumbnail camera contract if FileProvider is unavailable on device
    val takePicturePreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            previewBitmap = bitmap
            previewUri = null
            previewSourceIsCamera = true
            isPreviewActive = true
            errorMessage = null
        }
    }

    fun launchCamera() {
        try {
            val (file, uri) = createTempCameraFile()
            tempCameraFile = file
            tempCameraUri = uri
            takePictureLauncher.launch(uri)
        } catch (e: Exception) {
            // Fallback to preview contract
            takePicturePreviewLauncher.launch(null)
        }
    }

    // Camera permission request launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            errorMessage = "कैमरा अनुमति आवश्यक है (Camera permission is required)."
        }
    }

    // Gallery Photo Picker (Android 13+ Photo Picker with GetContent fallback)
    val pickVisualMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            previewUri = uri
            previewBitmap = null
            previewSourceIsCamera = false
            isPreviewActive = true
            errorMessage = null
        }
    }

    val getContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            previewUri = uri
            previewBitmap = null
            previewSourceIsCamera = false
            isPreviewActive = true
            errorMessage = null
        }
    }

    fun launchGallery() {
        try {
            pickVisualMediaLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } catch (e: Exception) {
            getContentLauncher.launch("image/*")
        }
    }

    // Permanent photo saver function
    fun confirmAndSavePhoto() {
        isSaving = true
        scope.launch {
            try {
                val permPath = withContext(Dispatchers.IO) {
                    val imagesDir = File(context.filesDir, "catering_images")
                    if (!imagesDir.exists()) imagesDir.mkdirs()
                    val targetFile = File(imagesDir, "photo_${System.currentTimeMillis()}.jpg")

                    if (previewBitmap != null) {
                        FileOutputStream(targetFile).use { out ->
                            previewBitmap!!.compress(Bitmap.CompressFormat.JPEG, 92, out)
                        }
                    } else if (previewUri != null) {
                        context.contentResolver.openInputStream(previewUri!!)?.use { input ->
                            FileOutputStream(targetFile).use { output ->
                                input.copyTo(output)
                            }
                        } ?: throw IllegalStateException("Cannot read image from URI")
                    } else {
                        throw IllegalStateException("No photo to save")
                    }
                    targetFile.absolutePath
                }

                withContext(Dispatchers.Main) {
                    isSaving = false
                    onPhotoSelected(permPath)
                    onDismiss()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isSaving = false
                    errorMessage = "फोटो सेव करने में त्रुटि: ${e.localizedMessage ?: "Unknown error"}"
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) onDismiss()
        },
        containerColor = CateringCardNavy,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isPreviewActive) "फोटो प्रीव्यू (Photo Preview)" else title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CateringTextPrimary
                )
                if (!isSaving) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = CateringTextSecondary)
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = CateringDangerRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                if (isSaving) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = CateringOrange)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "फोटो सुरक्षित रूप से सेव हो रही है...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CateringTextPrimary
                        )
                    }
                } else if (isPreviewActive) {
                    // PREVIEW STAGE WITH RETAKE AND CONFIRM BUTTONS
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CateringCardElevated)
                            .border(1.5.dp, CateringOrange, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (previewBitmap != null) {
                            Image(
                                bitmap = previewBitmap!!.asImageBitmap(),
                                contentDescription = "Captured Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (previewUri != null) {
                            AsyncImage(
                                model = previewUri,
                                contentDescription = "Selected Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "क्या यह फोटो ठीक है? पुष्टि करें या दोबारा लें।",
                        style = MaterialTheme.typography.bodySmall,
                        color = CateringTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Retake and Confirm action buttons (Requirement 2)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (previewSourceIsCamera) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasPermission) launchCamera() else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                } else {
                                    launchGallery()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("retake_photo_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(CateringBorder)
                            )
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = CateringOrange, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (previewSourceIsCamera) "पुनः लें (Retake)" else "दूसरा चुनें",
                                color = CateringTextPrimary,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = { confirmAndSavePhoto() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("confirm_photo_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CateringOrange)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("स्वीकारें (Confirm)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // SELECTION STAGE: SEPARATE TAKE PHOTO AND CHOOSE FROM GALLERY BUTTONS
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Take Photo Button (कैमरा से फोटो लें)
                        Button(
                            onClick = {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasPermission) {
                                    launchCamera()
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("take_photo_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CateringOrange)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Take Photo (कैमरा से लें)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        // 2. Choose from Gallery Button (गैलरी से चुनें)
                        Button(
                            onClick = { launchGallery() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("choose_gallery_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CateringCardElevated)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = CateringOrange, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Choose from Gallery (गैलरी से चुनें)", color = CateringTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        // 3. Remove Photo Button (if photo exists)
                        if (currentPhotoUri.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    onPhotoRemoved()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("remove_photo_button"),
                                shape = RoundedCornerShape(14.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(CateringDangerRed.copy(alpha = 0.6f))
                                )
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = CateringDangerRed, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("फोटो हटाएं (Remove Photo)", color = CateringDangerRed, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
