package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.CateringViewModel
import com.example.ui.components.TeamSyncDialog
import com.example.ui.screens.AddEditCounterScreen
import com.example.ui.screens.AddEditItemScreen
import com.example.ui.screens.CounterDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.CateringDarkNavy
import com.example.ui.theme.CateringDisplayManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CateringDisplayManagerTheme {
                val viewModel: CateringViewModel = viewModel()
                CateringApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CateringApp(viewModel: CateringViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val countersWithStats by viewModel.countersWithStats.collectAsStateWithLifecycle()
    val allItems by viewModel.allItems.collectAsStateWithLifecycle()
    val teamProfile by viewModel.teamProfile.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showTeamDialog by remember { mutableStateOf(false) }

    // Feedback message display
    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearFeedbackMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CateringDarkNavy)
        ) {
            when (uiState.currentScreen) {
                AppScreen.WELCOME -> {
                    WelcomeScreen(
                        onStartApp = { viewModel.navigateTo(AppScreen.HOME) }
                    )
                }

                AppScreen.HOME -> {
                    HomeScreen(
                        countersWithStats = countersWithStats,
                        searchQuery = uiState.counterSearchQuery,
                        onSearchQueryChange = { viewModel.setCounterSearchQuery(it) },
                        onCounterClick = { counterId -> viewModel.openCounterDetail(counterId) },
                        onAddCounterClick = { viewModel.openAddCounter() },
                        onDeleteCounter = { counterId -> viewModel.deleteCounter(counterId) },
                        onOpenTeamDialog = { showTeamDialog = true }
                    )
                }

                AppScreen.ADD_COUNTER -> {
                    AddEditCounterScreen(
                        onNavigateBack = { viewModel.navigateBack() },
                        onSaveCounter = { name, desc, uri ->
                            viewModel.saveCounter(name, desc, uri)
                        },
                        onSaveBitmap = { bitmap, callback ->
                            viewModel.saveBitmap(bitmap, "counter", callback)
                        },
                        onSaveUri = { uri, callback ->
                            viewModel.saveUri(uri, "counter", callback)
                        }
                    )
                }

                AppScreen.COUNTER_DETAIL -> {
                    val selectedCounterId = uiState.selectedCounterId ?: ""
                    val counter = countersWithStats.find { it.counter.counterId == selectedCounterId }?.counter
                        ?: uiState.selectedCounter
                    val counterItems = allItems.filter { it.counterId == selectedCounterId }

                    CounterDetailScreen(
                        counter = counter,
                        items = counterItems,
                        searchQuery = uiState.itemSearchQuery,
                        onSearchQueryChange = { viewModel.setItemSearchQuery(it) },
                        packingFilter = uiState.packingFilter,
                        onPackingFilterChange = { viewModel.setPackingFilter(it) },
                        selectedSection = uiState.selectedSection,
                        onSelectedSectionChange = { viewModel.setSelectedSection(it) },
                        onNavigateBack = { viewModel.navigateBack() },
                        onAddItemClick = { viewModel.openAddItem() },
                        onEditItemClick = { item -> viewModel.openEditItem(item) },
                        onDeleteItemClick = { itemId, name -> viewModel.deleteItem(itemId, name) },
                        onTogglePacking = { itemId, current -> viewModel.togglePacking(itemId, current) },
                        onResetPackingForCounter = { id -> viewModel.resetPackingForCurrentCounter(id) },
                        onUpdateCounterPhoto = { id, uri -> viewModel.updateCounterPhoto(id, uri) },
                        onDeleteCounter = { id -> viewModel.deleteCounter(id) },
                        onSaveBitmap = { bitmap, callback ->
                            viewModel.saveBitmap(bitmap, "counter", callback)
                        },
                        onSaveUri = { uri, callback ->
                            viewModel.saveUri(uri, "counter", callback)
                        }
                    )
                }

                AppScreen.ADD_ITEM, AppScreen.EDIT_ITEM -> {
                    val selectedCounterId = uiState.selectedCounterId ?: ""
                    val counter = countersWithStats.find { it.counter.counterId == selectedCounterId }?.counter
                        ?: uiState.selectedCounter

                    AddEditItemScreen(
                        counter = counter,
                        editingItem = uiState.editingItem,
                        onNavigateBack = { viewModel.navigateBack() },
                        onSaveItem = { name, quantity, unit, notes, uri, section, isPacked ->
                            if (uiState.currentScreen == AppScreen.EDIT_ITEM && uiState.editingItem != null) {
                                viewModel.updateItem(
                                    itemId = uiState.editingItem!!.itemId,
                                    counterId = selectedCounterId,
                                    name = name,
                                    quantity = quantity,
                                    unit = unit,
                                    notes = notes,
                                    imageUri = uri,
                                    section = section,
                                    isPacked = isPacked
                                )
                            } else {
                                viewModel.saveItem(
                                    counterId = selectedCounterId,
                                    name = name,
                                    quantity = quantity,
                                    unit = unit,
                                    notes = notes,
                                    imageUri = uri,
                                    section = section,
                                    isPacked = isPacked
                                )
                            }
                        },
                        onSaveBitmap = { bitmap, callback ->
                            viewModel.saveBitmap(bitmap, "item", callback)
                        },
                        onSaveUri = { uri, callback ->
                            viewModel.saveUri(uri, "item", callback)
                        }
                    )
                }

                AppScreen.TEAM_MANAGEMENT -> {
                    // Handled via Dialog
                }
            }

            // Team Sync Dialog
            if (showTeamDialog) {
                TeamSyncDialog(
                    teamProfile = teamProfile,
                    onDismiss = { showTeamDialog = false },
                    onCreateTeam = { teamName, adminName ->
                        viewModel.createTeam(teamName, adminName)
                        showTeamDialog = false
                    },
                    onJoinTeam = { code, memberName ->
                        viewModel.joinTeam(code, memberName)
                        showTeamDialog = false
                    },
                    onLeaveTeam = {
                        viewModel.leaveTeam()
                    }
                )
            }
        }
    }
}
