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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CounterEntity
import com.example.data.model.ItemEntity
import com.example.ui.components.CateringImage
import com.example.ui.components.PhotoPickerDialog
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    counter: CounterEntity?,
    editingItem: ItemEntity?,
    onNavigateBack: () -> Unit,
    onSaveItem: (
        name: String,
        quantity: Double,
        unit: String,
        notes: String,
        imageUri: String,
        section: String,
        isPacked: Boolean
    ) -> Unit,
    onSaveBitmap: (Bitmap, (String) -> Unit) -> Unit = { _, _ -> },
    onSaveUri: (Uri, (String) -> Unit) -> Unit = { _, _ -> }
) {
    BackHandler { onNavigateBack() }

    val isEditing = editingItem != null
    var itemName by remember { mutableStateOf(editingItem?.name ?: "") }
    var quantityStr by remember {
        mutableStateOf(
            if (editingItem != null) {
                if (editingItem.quantity % 1.0 == 0.0) editingItem.quantity.toInt().toString() else editingItem.quantity.toString()
            } else "1"
        )
    }
    var selectedUnit by remember { mutableStateOf(editingItem?.unit ?: "Pcs") }
    var customUnit by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf(editingItem?.notes ?: "") }
    var section by remember { mutableStateOf(editingItem?.section ?: "") }
    var imageUri by remember { mutableStateOf(editingItem?.imageUri ?: "") }
    var isPacked by remember { mutableStateOf(editingItem?.isPacked ?: false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var unitMenuExpanded by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }

    val standardUnits = listOf(
        "Pcs", "Box", "Set", "Meter", "Feet", "Kg", "Gram",
        "Pair", "Roll", "Packet", "Dozen", "Litre", "Other"
    )

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
            // Top Bar
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "सामान एडिट करें – ${counter?.name ?: ""}" else "नया सामान जोड़ें – ${counter?.name ?: ""}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        ),
                        color = CateringTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("item_form_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CateringTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CateringDarkNavy)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Photo upload box & buttons (Matching Video 1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Photo Preview Box
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CateringCardNavy)
                            .border(
                                width = 1.dp,
                                color = if (imageUri.isNotBlank()) CateringOrange else CateringBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { showPhotoPicker = true }
                            .testTag("item_photo_picker"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri.isNotBlank()) {
                            CateringImage(
                                imageUri = imageUri,
                                contentDescription = "Item Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                isItem = true
                            )
                            IconButton(
                                onClick = { imageUri = "" },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .background(Color(0xCC0D1117), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CateringOrange, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("फोटो जोड़ें", fontSize = 11.sp, color = CateringTextSecondary)
                            }
                        }
                    }

                    // Camera / Gallery Action Buttons (Requirement 2 & 3)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showPhotoPicker = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("item_take_photo_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CateringCardElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CateringOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (imageUri.isNotBlank()) "फोटो बदलें" else "Take Photo (कैमरा)",
                                color = CateringTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { showPhotoPicker = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("item_choose_gallery_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CateringCardElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = CateringOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Choose from gallery", color = CateringTextPrimary, fontSize = 12.sp)
                        }
                    }
                }

                // Quick Presets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Channel Light 10ft", "Support Stand", "Apple Box", "Extension Wire").forEach { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CateringCardNavy)
                                .clickable { itemName = preset }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(preset, fontSize = 11.sp, color = CateringOrange)
                        }
                    }
                }

                // Field B: Item Name
                Text(
                    text = "सामान का नाम *",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CateringTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = itemName,
                    onValueChange = {
                        itemName = it
                        if (errorMessage != null) errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_name_input"),
                    placeholder = { Text("उदा. Channel Light 10ft, Chef Coat", color = CateringTextMuted) },
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

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity and Unit Row (Video 1)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Quantity Field with +/- Buttons
                    Column(modifier = Modifier.weight(1.1f)) {
                        Text(
                            text = "मात्रा (Quantity) *",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = CateringTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CateringCardNavy)
                                .border(1.dp, CateringBorder, RoundedCornerShape(12.dp))
                        ) {
                            IconButton(
                                onClick = {
                                    val current = quantityStr.toDoubleOrNull() ?: 1.0
                                    if (current > 1.0) {
                                        val newQ = current - 1.0
                                        quantityStr = if (newQ % 1.0 == 0.0) newQ.toInt().toString() else newQ.toString()
                                    }
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = CateringOrange)
                            }

                            OutlinedTextField(
                                value = quantityStr,
                                onValueChange = { quantityStr = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("item_quantity_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = CateringTextPrimary,
                                    unfocusedTextColor = CateringTextPrimary,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )

                            IconButton(
                                onClick = {
                                    val current = quantityStr.toDoubleOrNull() ?: 0.0
                                    val newQ = current + 1.0
                                    quantityStr = if (newQ % 1.0 == 0.0) newQ.toInt().toString() else newQ.toString()
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", tint = CateringOrange)
                            }
                        }
                    }

                    // Unit Dropdown
                    Column(modifier = Modifier.weight(0.9f)) {
                        Text(
                            text = "इकाई (Unit)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = CateringTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CateringCardNavy)
                                    .border(1.dp, CateringBorder, RoundedCornerShape(12.dp))
                                    .clickable { unitMenuExpanded = true }
                                    .padding(horizontal = 14.dp)
                                    .testTag("unit_dropdown"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = selectedUnit,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = CateringTextPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Dropdown",
                                    tint = CateringOrange
                                )
                            }

                            DropdownMenu(
                                expanded = unitMenuExpanded,
                                onDismissRequest = { unitMenuExpanded = false },
                                modifier = Modifier.background(CateringCardElevated)
                            ) {
                                standardUnits.forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text(unit, color = CateringTextPrimary) },
                                        onClick = {
                                            selectedUnit = unit
                                            unitMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // If "Other" unit selected, show custom unit input
                if (selectedUnit == "Other") {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customUnit,
                        onValueChange = { customUnit = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("कस्टम इकाई (उदा. Bundle)", color = CateringTextMuted) },
                        singleLine = true,
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Field E: Notes / Description
                Text(
                    text = "विवरण / नोट्स (Notes)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CateringTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("item_notes_input"),
                    placeholder = { Text("उदा. Warm Light, Display के लिए।", color = CateringTextMuted) },
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

                Spacer(modifier = Modifier.height(16.dp))

                // Optional Section Tag
                Text(
                    text = "सेक्शन / केटेगरी (Category / Section)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CateringTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Lights", "Wires", "Connectors", "Disposables", "Dry Store").forEach { sec ->
                        val isSelected = section.equals(sec, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CateringOrange else CateringCardNavy)
                                .clickable { section = if (isSelected) "" else sec }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(sec, fontSize = 12.sp, color = if (isSelected) Color.White else CateringTextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checkbox: Packed Status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CateringCardNavy)
                        .clickable { isPacked = !isPacked }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isPacked,
                        onCheckedChange = { isPacked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = CateringPackedGreen,
                            uncheckedColor = CateringTextMuted,
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "सामान पैक किया हुआ है (Mark as Packed)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = CateringTextPrimary
                        )
                        Text(
                            text = if (isPacked) "स्थिति: Packed (तैयार)" else "स्थिति: Pending (पैक करना बाकी)",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isPacked) CateringPackedGreen else CateringTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Primary Orange Button: "सामान सेव करें"
                Button(
                    onClick = {
                        if (itemName.isBlank()) {
                            errorMessage = "कृपया सामान का नाम दर्ज करें"
                            return@Button
                        }
                        val qty = quantityStr.toDoubleOrNull() ?: 1.0
                        val finalUnit = if (selectedUnit == "Other" && customUnit.isNotBlank()) customUnit.trim() else selectedUnit
                        onSaveItem(
                            itemName.trim(),
                            qty,
                            finalUnit,
                            notes.trim(),
                            imageUri,
                            section.trim(),
                            isPacked
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("save_item_button"),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CateringOrange,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        text = if (isEditing) "अपडेट सेव करें" else "सामान सेव करें",
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
                title = "सामान की फोटो जोड़ें / बदलें",
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
