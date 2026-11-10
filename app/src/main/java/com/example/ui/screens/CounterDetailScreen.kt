package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CounterEntity
import com.example.data.model.ItemEntity
import com.example.ui.PackingFilter
import com.example.ui.components.CateringImage
import com.example.ui.components.PhotoPickerDialog
import com.example.ui.theme.CateringBorder
import com.example.ui.theme.CateringCardElevated
import com.example.ui.theme.CateringCardNavy
import com.example.ui.theme.CateringDangerRed
import com.example.ui.theme.CateringDarkNavy
import com.example.ui.theme.CateringOrange
import com.example.ui.theme.CateringPackedGreen
import com.example.ui.theme.CateringPackedGreenBg
import com.example.ui.theme.CateringPendingAmber
import com.example.ui.theme.CateringPendingAmberBg
import com.example.ui.theme.CateringTextMuted
import com.example.ui.theme.CateringTextPrimary
import com.example.ui.theme.CateringTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterDetailScreen(
    counter: CounterEntity?,
    items: List<ItemEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    packingFilter: PackingFilter,
    onPackingFilterChange: (PackingFilter) -> Unit,
    selectedSection: String,
    onSelectedSectionChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onAddItemClick: () -> Unit,
    onEditItemClick: (ItemEntity) -> Unit,
    onDeleteItemClick: (String, String) -> Unit,
    onTogglePacking: (String, Boolean) -> Unit,
    onResetPackingForCounter: (String) -> Unit,
    onUpdateCounterPhoto: (String, String) -> Unit,
    onDeleteCounter: (String) -> Unit,
    onSaveBitmap: (Bitmap, (String) -> Unit) -> Unit,
    onSaveUri: (Uri, (String) -> Unit) -> Unit
) {
    BackHandler { onNavigateBack() }

    if (counter == null) {
        Box(modifier = Modifier.fillMaxSize().background(CateringDarkNavy), contentAlignment = Alignment.Center) {
            Text("Counter लोड हो रहा है...", color = CateringTextSecondary)
        }
        return
    }

    var itemToDelete by remember { mutableStateOf<ItemEntity?>(null) }
    var showDeleteCounterDialog by remember { mutableStateOf(false) }
    var showResetPackingDialog by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }

    // Filter items based on counter query, packing filter, and section
    val filteredItems = remember(items, searchQuery, packingFilter, selectedSection) {
        items.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.notes.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (packingFilter) {
                PackingFilter.ALL -> true
                PackingFilter.PACKED -> item.isPacked
                PackingFilter.PENDING -> !item.isPacked
            }

            val matchesSection = selectedSection.isBlank() ||
                    item.section.equals(selectedSection, ignoreCase = true)

            matchesQuery && matchesFilter && matchesSection
        }
    }

    val totalCount = items.size
    val packedCount = items.count { it.isPacked }
    val pendingCount = items.count { !it.isPacked }
    val totalQty = items.sumOf { it.quantity }

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
                    Column {
                        Text(
                            text = counter.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = CateringTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "कुल सामान: $totalCount Items • ${if (totalQty % 1.0 == 0.0) totalQty.toInt() else "%.1f".format(totalQty)} Qty",
                            style = MaterialTheme.typography.bodySmall,
                            color = CateringOrange
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CateringTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showResetPackingDialog = true },
                        modifier = Modifier.testTag("reset_packing_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Reset Packing for Event",
                            tint = CateringTextSecondary
                        )
                    }
                    IconButton(
                        onClick = { showDeleteCounterDialog = true },
                        modifier = Modifier.testTag("delete_counter_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Counter",
                            tint = CateringDangerRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CateringDarkNavy)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Counter Hero Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CateringCardNavy)
                            .border(1.dp, CateringBorder, RoundedCornerShape(16.dp))
                    ) {
                        CateringImage(
                            imageUri = counter.imageUri,
                            contentDescription = counter.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (counter.description.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomStart)
                                    .background(Color(0xBB0D1117))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = counter.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CateringTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Main Two-Box Action Section (Reference Video 1 / Part 3)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // LEFT SIDE: Photo Upload Box ("फोटो डालें")
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showPhotoPicker = true }
                                .testTag("counter_upload_photo_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CateringCardNavy),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CateringBorder))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(CateringCardElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddAPhoto,
                                        contentDescription = "Upload Photo",
                                        tint = CateringOrange,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "फोटो डालें",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = CateringTextPrimary
                                )
                                Text(
                                    text = "Add Upload",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CateringTextSecondary
                                )
                            }
                        }

                        // RIGHT SIDE: Large Square Add Item Button ("सामान जोड़ें") with WHITE PLUS ICON
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onAddItemClick() }
                                .testTag("add_item_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CateringCardElevated),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CateringOrange))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(CateringOrange),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Clearly visible WHITE Plus (+) icon
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Item",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "सामान जोड़ें",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "नया आइटम",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CateringOrange
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Search Bar inside Counter
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("counter_item_search_field"),
                        placeholder = { Text("सामान खोजें (उदा. Channel Light)...", color = CateringTextMuted, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = CateringTextMuted)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = CateringTextMuted)
                                }
                            }
                        },
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
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Packing Status Checklist Filter Tabs (Part 8)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = packingFilter == PackingFilter.ALL,
                            onClick = { onPackingFilterChange(PackingFilter.ALL) },
                            label = { Text("All ($totalCount)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CateringOrange,
                                selectedLabelColor = Color.White,
                                containerColor = CateringCardNavy,
                                labelColor = CateringTextSecondary
                            )
                        )

                        FilterChip(
                            selected = packingFilter == PackingFilter.PACKED,
                            onClick = { onPackingFilterChange(PackingFilter.PACKED) },
                            label = { Text("Packed ($packedCount)") },
                            leadingIcon = {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CateringPackedGreen, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CateringPackedGreen,
                                selectedLabelColor = Color.White,
                                containerColor = CateringCardNavy,
                                labelColor = CateringPackedGreen
                            )
                        )

                        FilterChip(
                            selected = packingFilter == PackingFilter.PENDING,
                            onClick = { onPackingFilterChange(PackingFilter.PENDING) },
                            label = { Text("Pending ($pendingCount)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CateringPendingAmber,
                                selectedLabelColor = Color.White,
                                containerColor = CateringCardNavy,
                                labelColor = CateringPendingAmber
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Optional Categories / Sections (Part 9)
                item {
                    val sections = listOf("सभी (All)", "Lights", "Wires", "Connectors", "Stands/Risers", "Kitchen", "Disposables")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(sections) { sec ->
                            val isSelected = (sec == "सभी (All)" && selectedSection.isEmpty()) || selectedSection == sec
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CateringCardElevated else CateringCardNavy)
                                    .border(1.dp, if (isSelected) CateringOrange else CateringBorder, RoundedCornerShape(8.dp))
                                    .clickable {
                                        onSelectedSectionChange(if (sec == "सभी (All)") "" else sec)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = sec,
                                    fontSize = 12.sp,
                                    color = if (isSelected) CateringOrange else CateringTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Section Title
                item {
                    Text(
                        text = "इस Counter का सामान (${filteredItems.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CateringTextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Isolated Items List (Part 5, 6, 7)
                if (filteredItems.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CateringCardNavy)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (searchQuery.isNotBlank()) "खोजे गए नाम का कोई सामान नहीं है" else "इस Counter में अभी कोई सामान नहीं है",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CateringTextSecondary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onAddItemClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = CateringOrange)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("पहला सामान जोड़ें", color = Color.White)
                                }
                            }
                        }
                    }
                } else {
                    items(filteredItems, key = { it.itemId }) { item ->
                        ItemCard(
                            item = item,
                            onTogglePacking = { onTogglePacking(item.itemId, item.isPacked) },
                            onEditClick = { onEditItemClick(item) },
                            onDeleteClick = { itemToDelete = item }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }

    // Photo Picker Dialog with Camera preview, Retake/Confirm, and Gallery selection
    if (showPhotoPicker) {
        PhotoPickerDialog(
            title = "${counter.name} फोटो बदलें",
            currentPhotoUri = counter.imageUri,
            onDismiss = { showPhotoPicker = false },
            onPhotoSelected = { permanentPath ->
                onUpdateCounterPhoto(counter.counterId, permanentPath)
                showPhotoPicker = false
            },
            onPhotoRemoved = {
                onUpdateCounterPhoto(counter.counterId, "")
                showPhotoPicker = false
            }
        )
    }

    // Delete Item Confirmation Dialog (Part 7)
    itemToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("सामान हटाएं?", color = CateringTextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("क्या आप वाकई '${target.name}' को हटाना चाहते हैं?", color = CateringTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteItemClick(target.itemId, target.name)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CateringDangerRed)
                ) {
                    Text("हटाएं (Delete)", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("रद्द करें", color = CateringTextSecondary)
                }
            },
            containerColor = CateringCardElevated
        )
    }

    // Delete Counter Confirmation Dialog (Part 6)
    if (showDeleteCounterDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteCounterDialog = false },
            title = { Text("${counter.name} हटाएं?", color = CateringTextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("क्या आप '${counter.name}' और इसके सभी सामानों को हटाना चाहते हैं? यह क्रिया वापस नहीं की जा सकती।", color = CateringTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteCounterDialog = false
                        onDeleteCounter(counter.counterId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CateringDangerRed)
                ) {
                    Text("पूरा Counter हटाएं", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCounterDialog = false }) {
                    Text("रद्द करें", color = CateringTextSecondary)
                }
            },
            containerColor = CateringCardElevated
        )
    }

    // Reset Packing For Event Confirmation Dialog (Part 8)
    if (showResetPackingDialog) {
        AlertDialog(
            onDismissRequest = { showResetPackingDialog = false },
            title = { Text("नए इवेंट के लिए रीसेट करें?", color = CateringTextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("इस Counter के सभी सामानों का पैकिंग स्टेटस 'Pending' हो जाएगा। नाम और संख्या सुरक्षित रहेगी।", color = CateringTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showResetPackingDialog = false
                        onResetPackingForCounter(counter.counterId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CateringOrange)
                ) {
                    Text("रीसेट करें (Reset)", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetPackingDialog = false }) {
                    Text("रद्द करें", color = CateringTextSecondary)
                }
            },
            containerColor = CateringCardElevated
        )
    }
}

@Composable
fun ItemCard(
    item: ItemEntity,
    onTogglePacking: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("item_card_${item.itemId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CateringCardNavy),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (item.isPacked) CateringPackedGreen.copy(alpha = 0.5f) else CateringBorder
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Packed Status Checkbox (Part 8)
            Checkbox(
                checked = item.isPacked,
                onCheckedChange = { onTogglePacking() },
                colors = CheckboxDefaults.colors(
                    checkedColor = CateringPackedGreen,
                    uncheckedColor = CateringTextMuted,
                    checkmarkColor = Color.White
                ),
                modifier = Modifier.testTag("checkbox_${item.itemId}")
            )

            // Item Thumbnail Photo
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CateringCardElevated)
            ) {
                CateringImage(
                    imageUri = item.imageUri,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    isItem = true
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Item Information
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textDecoration = if (item.isPacked) TextDecoration.None else TextDecoration.None
                    ),
                    color = if (item.isPacked) CateringTextPrimary else CateringTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val qtyStr = if (item.quantity % 1.0 == 0.0) item.quantity.toInt().toString() else item.quantity.toString()
                    Text(
                        text = "Quantity: $qtyStr ${item.unit}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = CateringOrange
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Status Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (item.isPacked) CateringPackedGreenBg else CateringPendingAmberBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (item.isPacked) "पैक (Packed)" else "पेंडिंग (Pending)",
                            fontSize = 10.sp,
                            color = if (item.isPacked) CateringPackedGreen else CateringPendingAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (item.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = item.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = CateringTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Action Icons (Edit & Delete)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("edit_item_${item.itemId}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Item",
                        tint = CateringOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("delete_item_${item.itemId}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Item",
                        tint = CateringDangerRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
