package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CounterWithStats
import com.example.ui.components.CateringImage
import com.example.ui.theme.CateringBorder
import com.example.ui.theme.CateringCardElevated
import com.example.ui.theme.CateringCardNavy
import com.example.ui.theme.CateringDangerRed
import com.example.ui.theme.CateringDarkNavy
import com.example.ui.theme.CateringOrange
import com.example.ui.theme.CateringTextMuted
import com.example.ui.theme.CateringTextPrimary
import com.example.ui.theme.CateringTextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    countersWithStats: List<CounterWithStats>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCounterClick: (String) -> Unit,
    onAddCounterClick: () -> Unit,
    onDeleteCounter: (String) -> Unit,
    onOpenTeamDialog: () -> Unit
) {
    var counterToDelete by remember { mutableStateOf<CounterWithStats?>(null) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    val filteredCounters = remember(countersWithStats, searchQuery) {
        if (searchQuery.isBlank()) {
            countersWithStats
        } else {
            countersWithStats.filter {
                it.counter.name.contains(searchQuery, ignoreCase = true) ||
                        it.counter.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val totalCounters = countersWithStats.size
    val totalItems = countersWithStats.sumOf { it.totalItems }
    val totalQuantity = countersWithStats.sumOf { it.totalQuantity }

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
            // App Top Bar with Prominent KOKO BAKERS Logo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Extracted KOKO BAKERS Logo (kept in original proportions and colors)
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.koko_bakers_logo),
                        contentDescription = "KOKO BAKERS Logo",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(1.dp, CateringOrange.copy(alpha = 0.5f), CircleShape),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "KOKO BAKERS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 19.sp,
                                letterSpacing = 1.2.sp
                            ),
                            color = CateringTextPrimary
                        )
                        Text(
                            text = "Catering Display Manager",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = CateringOrange
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenTeamDialog,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("team_access_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Team Sync",
                            tint = CateringOrange
                        )
                    }

                    IconButton(
                        onClick = { isSearchExpanded = !isSearchExpanded },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = CateringTextPrimary
                        )
                    }
                }
            }

            // Expandable Search Bar
            if (isSearchExpanded || searchQuery.isNotBlank()) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_counters_field"),
                        placeholder = {
                            Text("Search Display / Counter खोजें...", color = CateringTextMuted)
                        },
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
                        shape = RoundedCornerShape(14.dp),
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
            }

            // Summary Stats Row (3 Metrics Badges as seen in reference videos)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatBadge(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Storefront,
                    count = totalCounters.toString(),
                    label = "Counters"
                )
                StatBadge(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Inventory2,
                    count = totalItems.toString(),
                    label = "Items"
                )
                StatBadge(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Layers,
                    count = if (totalQuantity % 1.0 == 0.0) totalQuantity.toInt().toString() else "%.1f".format(totalQuantity),
                    label = "Total Qty"
                )
            }

            // Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "मेरे Counters",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    ),
                    color = CateringTextPrimary
                )
                Text(
                    text = "${filteredCounters.size} उपलब्ध",
                    style = MaterialTheme.typography.bodySmall,
                    color = CateringTextSecondary
                )
            }

            // Counters 2-Column Responsive Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredCounters, key = { it.counter.counterId }) { item ->
                    CounterGridCard(
                        counterWithStats = item,
                        onClick = { onCounterClick(item.counter.counterId) },
                        onDeleteClick = { counterToDelete = item }
                    )
                }

                if (filteredCounters.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank()) "कोई Counter नहीं मिला" else "कोई Counter नहीं है",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CateringTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Large Orange Bottom Button: "+ नया Counter जोड़ें"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Button(
                onClick = onAddCounterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("add_counter_button"),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CateringOrange,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "+ नया Counter जोड़ें",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }

    // Delete Confirmation Dialog
    counterToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { counterToDelete = null },
            title = {
                Text(
                    text = "${target.counter.name} हटाएं?",
                    color = CateringTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "क्या आप वाकई '${target.counter.name}' और इसके सभी ${target.totalItems} सामानों को हटाना चाहते हैं?",
                    color = CateringTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCounter(target.counter.counterId)
                        counterToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CateringDangerRed)
                ) {
                    Text("हटाएं (Delete)", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { counterToDelete = null }) {
                    Text("रद्द करें", color = CateringTextSecondary)
                }
            },
            containerColor = CateringCardElevated
        )
    }
}

@Composable
fun StatBadge(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    count: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CateringCardNavy),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CateringBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CateringOrange,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                ),
                color = CateringTextPrimary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = CateringTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CounterGridCard(
    counterWithStats: CounterWithStats,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val counter = counterWithStats.counter

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("counter_card_${counter.counterId}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CateringCardNavy),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CateringBorder))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Photo Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
            ) {
                CateringImage(
                    imageUri = counter.imageUri,
                    contentDescription = counter.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Delete quick button on top-right
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .background(Color(0x880D1117), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Counter",
                        tint = CateringDangerRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Info Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = counter.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = CateringTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${counterWithStats.totalItems} Items",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = CateringOrange
                    )

                    val qtyText = if (counterWithStats.totalQuantity % 1.0 == 0.0) {
                        "${counterWithStats.totalQuantity.toInt()} Qty"
                    } else {
                        "%.1f Qty".format(counterWithStats.totalQuantity)
                    }

                    Text(
                        text = qtyText,
                        style = MaterialTheme.typography.bodySmall,
                        color = CateringTextSecondary
                    )
                }
            }
        }
    }
}
