package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CateringImage
import com.example.ui.components.PhotoPickerDialog
import com.example.ui.theme.CateringBorder
import com.example.ui.theme.CateringCardElevated
import com.example.ui.theme.CateringCardNavy
import com.example.ui.theme.CateringDangerRed
import com.example.ui.theme.CateringDarkNavy
import com.example.ui.theme.CateringOrange
import com.example.ui.theme.CateringTextMuted
import com.example.ui.theme.CateringTextPrimary
import com.example.ui.theme.CateringTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCounterScreen(
    onNavigateBack: () -> Unit,
    onSaveCounter: (name: String, description: String, imageUri: String) -> Unit,
    onSaveBitmap: (Bitmap, (String) -> Unit) -> Unit = { _, _ -> },
    onSaveUri: (Uri, (String) -> Unit) -> Unit = { _, _ -> }
) {
    BackHandler { onNavigateBack() }

    var counterName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPhotoPicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CateringDarkNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Top Bar
            TopAppBar(
                title = {
                    Text(
                        text = "नया Counter जोड़ें",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = CateringTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CateringTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CateringDarkNavy
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Large Photo Box (Matching Video 2)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CateringCardNavy)
                        .border(
                            width = 1.5.dp,
                            color = if (imageUri.isNotBlank()) CateringOrange else CateringOrange.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { showPhotoPicker = true }
                        .testTag("counter_photo_box"),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri.isNotBlank()) {
                        CateringImage(
                            imageUri = imageUri,
                            contentDescription = "Counter Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Floating action badge to change photo
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xDD0D1117))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = CateringOrange, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("फोटो बदलें (Change)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Remove photo button
                        IconButton(
                            onClick = { imageUri = "" },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .background(Color(0xCC0D1117), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove photo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(CateringCardElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Camera",
                                    tint = CateringOrange,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Counter की फोटो जोड़ें",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = CateringTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Take Photo या Choose from Gallery",
                                style = MaterialTheme.typography.bodySmall,
                                color = CateringTextSecondary
                            )
                        }
                    }
                }

                // Explicit Take Photo and Choose from Gallery buttons (Requirement 2 & 3)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showPhotoPicker = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("choose_photo_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CateringCardElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CateringOrange, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (imageUri.isNotBlank()) "फोटो बदलें" else "फोटो जोड़ें",
                            color = CateringTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (imageUri.isNotBlank()) {
                        OutlinedButton(
                            onClick = { imageUri = "" },
                            modifier = Modifier
                                .weight(0.7f)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(CateringDangerRed.copy(alpha = 0.7f))
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = CateringDangerRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("हटाएं", color = CateringDangerRed, fontSize = 12.sp)
                        }
                    }
                }

                // Preset Suggestions
                Text(
                    text = "त्वरित नाम सुझाव (Quick Presets):",
                    style = MaterialTheme.typography.labelMedium,
                    color = CateringTextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Pizza Van", "Sushi Van", "Avocado Bar", "Tapas Bar").forEach { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CateringCardElevated)
                                .clickable {
                                    counterName = preset
                                    if (preset == "Pizza Van") imageUri = "res:///drawable/pizza_van_banner"
                                    if (preset == "Sushi Van") imageUri = "res:///drawable/sushi_van_banner"
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(preset, fontSize = 12.sp, color = CateringOrange)
                        }
                    }
                }

                // Counter Name Input
                Text(
                    text = "Counter का नाम *",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CateringTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = counterName,
                    onValueChange = {
                        counterName = it
                        if (errorMessage != null) errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("counter_name_input"),
                    placeholder = { Text("उदा. Pizza Van, Dimsum Counter", color = CateringTextMuted) },
                    singleLine = true,
                    isError = errorMessage != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CateringOrange,
                        unfocusedBorderColor = CateringBorder,
                        focusedTextColor = CateringTextPrimary,
                        unfocusedTextColor = CateringTextPrimary,
                        focusedContainerColor = CateringCardNavy,
                        unfocusedContainerColor = CateringCardNavy
                    )
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Description Input
                Text(
                    text = "विवरण (वैकल्पिक)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CateringTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("counter_desc_input"),
                    placeholder = { Text("उदा. Pizza Live Station & Display Setup", color = CateringTextMuted) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CateringOrange,
                        unfocusedBorderColor = CateringBorder,
                        focusedTextColor = CateringTextPrimary,
                        unfocusedTextColor = CateringTextPrimary,
                        focusedContainerColor = CateringCardNavy,
                        unfocusedContainerColor = CateringCardNavy
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Orange Save Button
                Button(
                    onClick = {
                        if (counterName.isBlank()) {
                            errorMessage = "कृपया Counter का नाम दर्ज करें"
                        } else {
                            onSaveCounter(counterName.trim(), description.trim(), imageUri)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("save_counter_button"),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CateringOrange,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        text = "Counter सेव करें",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Photo Picker & Camera with Preview, Retake & Confirm Dialog
        if (showPhotoPicker) {
            PhotoPickerDialog(
                title = "Counter की फोटो जोड़ें / बदलें",
                currentPhotoUri = imageUri,
                onDismiss = { showPhotoPicker = false },
                onPhotoSelected = { permanentPath ->
                    imageUri = permanentPath
                },
                onPhotoRemoved = {
                    imageUri = ""
                }
            )
        }
    }
}
