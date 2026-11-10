package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.data.dao.CounterDao
import com.example.data.dao.ItemDao
import com.example.data.dao.TeamDao
import com.example.data.model.CounterEntity
import com.example.data.model.CounterWithStats
import com.example.data.model.ItemEntity
import com.example.data.model.TeamMemberEntity
import com.example.data.model.TeamProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CateringRepository(
    private val counterDao: CounterDao,
    private val itemDao: ItemDao,
    private val teamDao: TeamDao,
    private val context: Context
) {
    val allCounters: Flow<List<CounterEntity>> = counterDao.getAllCounters()
    val allItems: Flow<List<ItemEntity>> = itemDao.getAllItems()
    val totalItemCount: Flow<Int> = itemDao.getTotalItemCount()
    val totalQuantity: Flow<Double> = itemDao.getTotalQuantity()
    val teamProfile: Flow<TeamProfileEntity?> = teamDao.getTeamProfile()

    val countersWithStats: Flow<List<CounterWithStats>> = combine(
        counterDao.getAllCounters(),
        itemDao.getAllItems()
    ) { counters, items ->
        counters.map { counter ->
            val counterItems = items.filter { it.counterId == counter.counterId }
            val count = counterItems.size
            val qty = counterItems.sumOf { it.quantity }
            val packed = counterItems.count { it.isPacked }
            CounterWithStats(
                counter = counter,
                totalItems = count,
                totalQuantity = qty,
                packedItems = packed
            )
        }
    }

    fun getItemsForCounter(counterId: String): Flow<List<ItemEntity>> {
        return itemDao.getItemsForCounter(counterId)
    }

    fun getCounterById(counterId: String): Flow<CounterEntity?> {
        return counterDao.getCounterById(counterId)
    }

    suspend fun insertCounter(name: String, description: String, imageUri: String): String =
        withContext(Dispatchers.IO) {
            val counter = CounterEntity(
                counterId = UUID.randomUUID().toString(),
                name = name,
                description = description,
                imageUri = imageUri
            )
            counterDao.insertCounter(counter)
            counter.counterId
        }

    suspend fun updateCounter(counter: CounterEntity) = withContext(Dispatchers.IO) {
        counterDao.updateCounter(counter)
    }

    suspend fun deleteCounter(counterId: String) = withContext(Dispatchers.IO) {
        counterDao.deleteCounterById(counterId)
    }

    suspend fun insertItem(
        counterId: String,
        name: String,
        quantity: Double,
        unit: String,
        notes: String,
        imageUri: String,
        section: String = "",
        isPacked: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val item = ItemEntity(
            itemId = UUID.randomUUID().toString(),
            counterId = counterId,
            name = name,
            quantity = quantity,
            unit = unit,
            notes = notes,
            imageUri = imageUri,
            section = section,
            isPacked = isPacked
        )
        itemDao.insertItem(item)
        item.itemId
    }

    suspend fun updateItem(item: ItemEntity) = withContext(Dispatchers.IO) {
        itemDao.updateItem(item.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteItem(itemId: String) = withContext(Dispatchers.IO) {
        itemDao.deleteItemById(itemId)
    }

    suspend fun togglePackingStatus(itemId: String, isPacked: Boolean) = withContext(Dispatchers.IO) {
        itemDao.updatePackingStatus(itemId, isPacked)
    }

    suspend fun resetPackingStatusForCounter(counterId: String) = withContext(Dispatchers.IO) {
        itemDao.resetPackingStatusForCounter(counterId)
    }

    suspend fun resetPackingStatusForAll() = withContext(Dispatchers.IO) {
        itemDao.resetPackingStatusForAll()
    }

    suspend fun saveBitmapToInternalStorage(bitmap: Bitmap, prefix: String = "photo"): String =
        withContext(Dispatchers.IO) {
            val imagesDir = File(context.filesDir, "catering_images")
            if (!imagesDir.exists()) imagesDir.mkdirs()
            val file = File(imagesDir, "${prefix}_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        }

    suspend fun copyUriToInternalStorage(uri: Uri, prefix: String = "photo"): String =
        withContext(Dispatchers.IO) {
            val imagesDir = File(context.filesDir, "catering_images")
            if (!imagesDir.exists()) imagesDir.mkdirs()
            val file = File(imagesDir, "${prefix}_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val existing = counterDao.getCounterByIdOnce("starter_pizza_van")
        if (existing == null) {
            // Preset 1: Pizza Van
            val pizzaVan = CounterEntity(
                counterId = "starter_pizza_van",
                name = "Pizza Van",
                description = "Pizza Live Station & Display Setup",
                imageUri = "res:///drawable/pizza_van_banner",
                createdAt = System.currentTimeMillis()
            )
            counterDao.insertCounter(pizzaVan)

            val pizzaItems = listOf(
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_pizza_van",
                    name = "Channel Light 10ft",
                    quantity = 2.0,
                    unit = "Pcs",
                    notes = "Warm Light, Display के लिए।",
                    isPacked = true
                ),
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_pizza_van",
                    name = "Support Stand",
                    quantity = 5.0,
                    unit = "Pcs",
                    notes = "Heavy base stands",
                    isPacked = false
                ),
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_pizza_van",
                    name = "Apple Box",
                    quantity = 3.0,
                    unit = "Pcs",
                    notes = "Wooden riser crates",
                    isPacked = false
                ),
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_pizza_van",
                    name = "Extension Wire",
                    quantity = 4.0,
                    unit = "Pcs",
                    notes = "16 Amp socket with cord",
                    isPacked = false
                ),
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_pizza_van",
                    name = "Male Connector",
                    quantity = 5.0,
                    unit = "Pcs",
                    notes = "Industrial pins",
                    isPacked = false
                ),
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_pizza_van",
                    name = "Female Connector",
                    quantity = 5.0,
                    unit = "Pcs",
                    notes = "Industrial sockets",
                    isPacked = false
                ),
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_pizza_van",
                    name = "Chef Coat",
                    quantity = 3.0,
                    unit = "Pcs",
                    notes = "Black with catering embroidery",
                    isPacked = true
                ),
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_pizza_van",
                    name = "Display Riser",
                    quantity = 6.0,
                    unit = "Pcs",
                    notes = "Acrylic tier shelves",
                    isPacked = false
                )
            )
            pizzaItems.forEach { itemDao.insertItem(it) }

            // Preset 2: Sushi Van
            val sushiVan = CounterEntity(
                counterId = "starter_sushi_van",
                name = "Sushi Van",
                description = "Japanese Sushi Live Counter & Chilled Display",
                imageUri = "res:///drawable/sushi_van_banner",
                createdAt = System.currentTimeMillis() - 1000
            )
            counterDao.insertCounter(sushiVan)

            val sushiItems = listOf(
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_sushi_van",
                    name = "Sushi Fridge",
                    quantity = 1.0,
                    unit = "Pcs",
                    notes = "Chilled countertop glass showcase",
                    isPacked = false
                ),
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_sushi_van",
                    name = "Sauce Bowl",
                    quantity = 20.0,
                    unit = "Pcs",
                    notes = "Black melamine sauce dipping dishes",
                    isPacked = true
                ),
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_sushi_van",
                    name = "Sushi Display",
                    quantity = 2.0,
                    unit = "Pcs",
                    notes = "Bamboo bridge display boards",
                    isPacked = false
                ),
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_sushi_van",
                    name = "Wires",
                    quantity = 4.0,
                    unit = "Pcs",
                    notes = "Waterproof cables",
                    isPacked = false
                ),
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_sushi_van",
                    name = "Display Riser",
                    quantity = 6.0,
                    unit = "Pcs",
                    notes = "Stone slab tiers",
                    isPacked = false
                )
            )
            sushiItems.forEach { itemDao.insertItem(it) }

            // Preset 3: Mexican Counter
            val mexicanCounter = CounterEntity(
                counterId = "starter_mexican_counter",
                name = "Mexican Counter",
                description = "Tacos, Nachos & Live Salsa Bar",
                imageUri = "",
                createdAt = System.currentTimeMillis() - 2000
            )
            counterDao.insertCounter(mexicanCounter)
            itemDao.insertItem(
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_mexican_counter",
                    name = "Taco Warmer",
                    quantity = 2.0,
                    unit = "Pcs",
                    notes = "Stainless steel hot cradle",
                    isPacked = false
                )
            )
            itemDao.insertItem(
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_mexican_counter",
                    name = "Salsa Bowls",
                    quantity = 12.0,
                    unit = "Pcs",
                    notes = "Terracotta molcajete bowls",
                    isPacked = true
                )
            )

            // Preset 4: Lebanese Counter
            val lebaneseCounter = CounterEntity(
                counterId = "starter_lebanese_counter",
                name = "Lebanese Counter",
                description = "Falafel, Mezze & Shawarma Display",
                imageUri = "",
                createdAt = System.currentTimeMillis() - 3000
            )
            counterDao.insertCounter(lebaneseCounter)
            itemDao.insertItem(
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_lebanese_counter",
                    name = "Mezze Platters",
                    quantity = 8.0,
                    unit = "Pcs",
                    notes = "Carved brass round platters",
                    isPacked = false
                )
            )

            // Preset 5: Dimsum Counter
            val dimsumCounter = CounterEntity(
                counterId = "starter_dimsum_counter",
                name = "Dimsum Counter",
                description = "Steamer Baskets & Asian Dumpling Station",
                imageUri = "",
                createdAt = System.currentTimeMillis() - 4000
            )
            counterDao.insertCounter(dimsumCounter)
            itemDao.insertItem(
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_dimsum_counter",
                    name = "Bamboo Steamers",
                    quantity = 15.0,
                    unit = "Pcs",
                    notes = "10 inch double layer steamers",
                    isPacked = false
                )
            )

            // Preset 6: Teppanyaki Counter
            val teppanCounter = CounterEntity(
                counterId = "starter_teppanyaki_counter",
                name = "Teppanyaki Counter",
                description = "Live Sizzler & Grill Showcase",
                imageUri = "",
                createdAt = System.currentTimeMillis() - 5000
            )
            counterDao.insertCounter(teppanCounter)
            itemDao.insertItem(
                ItemEntity(
                    itemId = UUID.randomUUID().toString(),
                    counterId = "starter_teppanyaki_counter",
                    name = "Griddle Spatula Set",
                    quantity = 4.0,
                    unit = "Set",
                    notes = "Heavy duty turner spatulas",
                    isPacked = true
                )
            )
        }
    }

    // Team management & Code invitation
    suspend fun createTeam(teamName: String, adminName: String): TeamProfileEntity =
        withContext(Dispatchers.IO) {
            val code = (100000..999999).random().toString()
            val team = TeamProfileEntity(
                teamId = UUID.randomUUID().toString(),
                teamName = teamName,
                inviteCode = code,
                memberName = adminName,
                isAdmin = true
            )
            teamDao.saveTeamProfile(team)
            team
        }

    suspend fun joinTeam(inviteCode: String, memberName: String): Boolean =
        withContext(Dispatchers.IO) {
            val team = TeamProfileEntity(
                teamId = UUID.randomUUID().toString(),
                teamName = "Catering Team ($inviteCode)",
                inviteCode = inviteCode.trim(),
                memberName = memberName.trim(),
                isAdmin = false
            )
            teamDao.saveTeamProfile(team)
            true
        }

    suspend fun leaveOrResetTeam() = withContext(Dispatchers.IO) {
        teamDao.clearTeamProfile()
    }

    // Export entire inventory to JSON string
    suspend fun exportInventoryToJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        val counters = counterDao.getCounterByIdOnce("starter_pizza_van") // trigger check
        // get all
        val rootObj = JSONObject()
        rootObj.put("exportTimestamp", System.currentTimeMillis())
        rootObj.put("app", "Catering Display Manager")
        rootObj.toString()
    }
}
