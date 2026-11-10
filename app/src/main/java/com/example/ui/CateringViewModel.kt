package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CateringDatabase
import com.example.data.CateringRepository
import com.example.data.model.CounterEntity
import com.example.data.model.CounterWithStats
import com.example.data.model.ItemEntity
import com.example.data.model.TeamProfileEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    WELCOME,
    HOME,
    ADD_COUNTER,
    COUNTER_DETAIL,
    ADD_ITEM,
    EDIT_ITEM,
    TEAM_MANAGEMENT
}

enum class PackingFilter {
    ALL,
    PACKED,
    PENDING
}

data class CateringUiState(
    val currentScreen: AppScreen = AppScreen.WELCOME,
    val selectedCounterId: String? = null,
    val selectedCounter: CounterEntity? = null,
    val editingItem: ItemEntity? = null,
    val counterSearchQuery: String = "",
    val itemSearchQuery: String = "",
    val selectedSection: String = "",
    val packingFilter: PackingFilter = PackingFilter.ALL,
    val feedbackMessage: String? = null,
    val isTeamDialogOpen: Boolean = false
)

class CateringViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CateringRepository

    private val _uiState = MutableStateFlow(CateringUiState())
    val uiState: StateFlow<CateringUiState> = _uiState.asStateFlow()

    private val screenBackStack = mutableListOf<AppScreen>()

    init {
        val db = CateringDatabase.getDatabase(application)
        repository = CateringRepository(
            counterDao = db.counterDao(),
            itemDao = db.itemDao(),
            teamDao = db.teamDao(),
            context = application
        )

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val countersWithStats: StateFlow<List<CounterWithStats>> = repository.countersWithStats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val teamProfile: StateFlow<TeamProfileEntity?> = repository.teamProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allItems: StateFlow<List<ItemEntity>> = repository.allItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Navigation methods
    fun navigateTo(screen: AppScreen) {
        val current = _uiState.value.currentScreen
        if (current != screen) {
            screenBackStack.add(current)
        }
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            val prev = screenBackStack.removeAt(screenBackStack.size - 1)
            _uiState.value = _uiState.value.copy(
                currentScreen = prev,
                editingItem = if (prev != AppScreen.EDIT_ITEM) null else _uiState.value.editingItem
            )
            return true
        } else if (_uiState.value.currentScreen != AppScreen.HOME && _uiState.value.currentScreen != AppScreen.WELCOME) {
            _uiState.value = _uiState.value.copy(currentScreen = AppScreen.HOME)
            return true
        }
        return false
    }

    fun openCounterDetail(counterId: String) {
        viewModelScope.launch {
            val counter = countersWithStats.value.find { it.counter.counterId == counterId }?.counter
            _uiState.value = _uiState.value.copy(
                selectedCounterId = counterId,
                selectedCounter = counter,
                itemSearchQuery = "",
                selectedSection = "",
                packingFilter = PackingFilter.ALL
            )
            navigateTo(AppScreen.COUNTER_DETAIL)
        }
    }

    fun openAddCounter() {
        navigateTo(AppScreen.ADD_COUNTER)
    }

    fun openAddItem() {
        _uiState.value = _uiState.value.copy(editingItem = null)
        navigateTo(AppScreen.ADD_ITEM)
    }

    fun openEditItem(item: ItemEntity) {
        _uiState.value = _uiState.value.copy(editingItem = item)
        navigateTo(AppScreen.EDIT_ITEM)
    }

    fun setCounterSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(counterSearchQuery = query)
    }

    fun setItemSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(itemSearchQuery = query)
    }

    fun setPackingFilter(filter: PackingFilter) {
        _uiState.value = _uiState.value.copy(packingFilter = filter)
    }

    fun setSelectedSection(section: String) {
        _uiState.value = _uiState.value.copy(selectedSection = section)
    }

    fun clearFeedbackMessage() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }

    // Database Actions
    fun saveCounter(name: String, description: String, imageUri: String) {
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(feedbackMessage = "कृपया Counter का नाम दर्ज करें")
            return
        }
        viewModelScope.launch {
            val id = repository.insertCounter(name.trim(), description.trim(), imageUri)
            _uiState.value = _uiState.value.copy(
                feedbackMessage = "$name Counter सफलता से सेव किया गया!"
            )
            navigateBack()
        }
    }

    fun updateCounterPhoto(counterId: String, newImageUri: String) {
        viewModelScope.launch {
            val counter = countersWithStats.value.find { it.counter.counterId == counterId }?.counter
            if (counter != null) {
                repository.updateCounter(counter.copy(imageUri = newImageUri))
                _uiState.value = _uiState.value.copy(
                    selectedCounter = counter.copy(imageUri = newImageUri),
                    feedbackMessage = "Counter फोटो अपडेट हो गई!"
                )
            }
        }
    }

    fun deleteCounter(counterId: String) {
        viewModelScope.launch {
            val counterName = countersWithStats.value.find { it.counter.counterId == counterId }?.counter?.name ?: "Counter"
            repository.deleteCounter(counterId)
            _uiState.value = _uiState.value.copy(
                feedbackMessage = "$counterName हटा दिया गया",
                selectedCounterId = null,
                selectedCounter = null
            )
            _uiState.value = _uiState.value.copy(currentScreen = AppScreen.HOME)
        }
    }

    fun saveItem(
        counterId: String,
        name: String,
        quantity: Double,
        unit: String,
        notes: String,
        imageUri: String,
        section: String,
        isPacked: Boolean
    ) {
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(feedbackMessage = "कृपया सामान का नाम दर्ज करें")
            return
        }
        viewModelScope.launch {
            repository.insertItem(
                counterId = counterId,
                name = name.trim(),
                quantity = quantity,
                unit = unit,
                notes = notes.trim(),
                imageUri = imageUri,
                section = section,
                isPacked = isPacked
            )
            _uiState.value = _uiState.value.copy(
                feedbackMessage = "$name सामान जोड़ दिया गया!"
            )
            navigateBack()
        }
    }

    fun updateItem(
        itemId: String,
        counterId: String,
        name: String,
        quantity: Double,
        unit: String,
        notes: String,
        imageUri: String,
        section: String,
        isPacked: Boolean
    ) {
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(feedbackMessage = "कृपया सामान का नाम दर्ज करें")
            return
        }
        viewModelScope.launch {
            val updated = ItemEntity(
                itemId = itemId,
                counterId = counterId,
                name = name.trim(),
                quantity = quantity,
                unit = unit,
                notes = notes.trim(),
                imageUri = imageUri,
                section = section,
                isPacked = isPacked
            )
            repository.updateItem(updated)
            _uiState.value = _uiState.value.copy(
                feedbackMessage = "$name सामान अपडेट हो गया!",
                editingItem = null
            )
            navigateBack()
        }
    }

    fun deleteItem(itemId: String, itemName: String) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
            _uiState.value = _uiState.value.copy(
                feedbackMessage = "$itemName हटा दिया गया"
            )
        }
    }

    fun togglePacking(itemId: String, currentPacked: Boolean) {
        viewModelScope.launch {
            repository.togglePackingStatus(itemId, !currentPacked)
        }
    }

    fun resetPackingForCurrentCounter(counterId: String) {
        viewModelScope.launch {
            repository.resetPackingStatusForCounter(counterId)
            _uiState.value = _uiState.value.copy(
                feedbackMessage = "इस Counter की पैकिंग स्थिति रीसेट हो गई (New Event Ready)"
            )
        }
    }

    fun resetAllPacking() {
        viewModelScope.launch {
            repository.resetPackingStatusForAll()
            _uiState.value = _uiState.value.copy(
                feedbackMessage = "सभी सामानों की पैकिंग स्थिति नए इवेंट के लिए रीसेट हो गई"
            )
        }
    }

    // Photo helpers
    fun saveBitmap(bitmap: Bitmap, prefix: String, onSaved: (String) -> Unit) {
        viewModelScope.launch {
            val path = repository.saveBitmapToInternalStorage(bitmap, prefix)
            onSaved(path)
        }
    }

    fun saveUri(uri: Uri, prefix: String, onSaved: (String) -> Unit) {
        viewModelScope.launch {
            val path = repository.copyUriToInternalStorage(uri, prefix)
            onSaved(path)
        }
    }

    // Team management
    fun createTeam(teamName: String, adminName: String) {
        viewModelScope.launch {
            val profile = repository.createTeam(teamName, adminName)
            _uiState.value = _uiState.value.copy(
                feedbackMessage = "टीम '${profile.teamName}' बनाई गई! कोड: ${profile.inviteCode}"
            )
        }
    }

    fun joinTeam(code: String, memberName: String) {
        viewModelScope.launch {
            repository.joinTeam(code, memberName)
            _uiState.value = _uiState.value.copy(
                feedbackMessage = "टीम में सफलतापूर्वक जुड़ गए!"
            )
        }
    }

    fun leaveTeam() {
        viewModelScope.launch {
            repository.leaveOrResetTeam()
            _uiState.value = _uiState.value.copy(
                feedbackMessage = "टीम छोड़ दी गई"
            )
        }
    }
}
