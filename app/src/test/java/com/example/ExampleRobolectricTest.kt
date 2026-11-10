package com.example

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CateringDatabase
import com.example.data.CateringRepository
import com.example.data.model.CounterEntity
import com.example.data.model.ItemEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: CateringDatabase
    private lateinit var repository: CateringRepository
    private lateinit var context: Context

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CateringDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CateringRepository(
            counterDao = database.counterDao(),
            itemDao = database.itemDao(),
            teamDao = database.teamDao(),
            context = context
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun testAppNameString() {
        val appName = context.getString(R.string.app_name)
        assertEquals("Catering Display Manager", appName)
    }

    @Test
    fun testKokoBakersLogoResource() {
        val logoDrawable = ContextCompat.getDrawable(context, R.drawable.koko_bakers_logo)
        assertNotNull("KOKO BAKERS logo drawable must be present in resources", logoDrawable)
    }

    @Test
    fun testPhotoManagement_PermanentSaving_Update_And_Removal(): Unit = runBlocking {
        // 1. Create a sample Bitmap to simulate camera capture or gallery selection
        val testBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        // 2. Save permanently to internal storage
        val savedPath = repository.saveBitmapToInternalStorage(testBitmap, "test_counter")
        assertNotNull(savedPath)
        val savedFile = File(savedPath)
        assertTrue("Saved image file must exist in permanent internal storage", savedFile.exists())
        assertTrue("Saved image file must not be empty", savedFile.length() > 0)

        // 3. Create Counter with this photo
        val counterId = repository.insertCounter(
            name = "Pizza Van",
            description = "Pizza Station",
            imageUri = savedPath
        )
        val loadedCounter = repository.getCounterById(counterId).first()
        assertEquals(savedPath, loadedCounter?.imageUri)

        // 4. Update Photo (Change Photo option)
        val newBitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888)
        val newSavedPath = repository.saveBitmapToInternalStorage(newBitmap, "test_counter_updated")
        repository.updateCounter(loadedCounter!!.copy(imageUri = newSavedPath))
        val updatedCounter = repository.getCounterById(counterId).first()
        assertEquals(newSavedPath, updatedCounter?.imageUri)

        // 5. Remove Photo option
        repository.updateCounter(updatedCounter!!.copy(imageUri = ""))
        val removedPhotoCounter = repository.getCounterById(counterId).first()
        assertEquals("", removedPhotoCounter?.imageUri)

        // 6. Item Photo Management
        val itemId = repository.insertItem(
            counterId = counterId,
            name = "Channel Light 10ft",
            quantity = 2.0,
            unit = "Pcs",
            notes = "Warm Light",
            imageUri = newSavedPath,
            isPacked = false
        )
        val items = repository.getItemsForCounter(counterId).first()
        val item = items.first { it.itemId == itemId }
        assertEquals(newSavedPath, item.imageUri)

        // Clean up test file
        savedFile.delete()
        File(newSavedPath).delete()
        Unit
    }

    @Test
    fun testCounterAndItemIsolation_Tests1to11(): Unit = runBlocking {
        // TEST 1: Create Pizza Van and save it. Confirm it appears on the Home Page.
        val pizzaVanId = repository.insertCounter(
            name = "Pizza Van",
            description = "Pizza Live Station & Display Setup",
            imageUri = "pizza_van_test.jpg"
        )
        assertNotNull(pizzaVanId)
        val counters = repository.allCounters.first()
        assertTrue(counters.any { it.name == "Pizza Van" && it.counterId == pizzaVanId })

        // TEST 2: Confirm its separate detail page / counter query returns the counter.
        val pizzaCounter = repository.getCounterById(pizzaVanId).first()
        assertNotNull(pizzaCounter)
        assertEquals("Pizza Van", pizzaCounter?.name)

        // TEST 3: Upload / update photo.
        repository.updateCounter(pizzaCounter!!.copy(imageUri = "new_photo_path.jpg"))
        val updatedCounter = repository.getCounterById(pizzaVanId).first()
        assertEquals("new_photo_path.jpg", updatedCounter?.imageUri)

        // TEST 4: Add Channel Light 10ft, quantity 2, unit Pcs and save it.
        val lightId = repository.insertItem(
            counterId = pizzaVanId,
            name = "Channel Light 10ft",
            quantity = 2.0,
            unit = "Pcs",
            notes = "Warm Light, Display के लिए।",
            imageUri = "",
            isPacked = false
        )

        // TEST 5: Add Support Stand, quantity 5, unit Pcs and save it.
        val standId = repository.insertItem(
            counterId = pizzaVanId,
            name = "Support Stand",
            quantity = 5.0,
            unit = "Pcs",
            notes = "Heavy stand",
            imageUri = "",
            isPacked = false
        )

        // TEST 6: Confirm both items appear as two separate cards.
        val pizzaItems = repository.getItemsForCounter(pizzaVanId).first()
        assertEquals(2, pizzaItems.size)
        assertTrue(pizzaItems.any { it.itemId == lightId && it.name == "Channel Light 10ft" })
        assertTrue(pizzaItems.any { it.itemId == standId && it.name == "Support Stand" })

        // TEST 7: Edit Channel Light quantity from 2 to 3. Confirm update is saved.
        val lightItem = pizzaItems.first { it.itemId == lightId }
        repository.updateItem(lightItem.copy(quantity = 3.0))
        val pizzaItemsAfterEdit = repository.getItemsForCounter(pizzaVanId).first()
        assertEquals(3.0, pizzaItemsAfterEdit.first { it.itemId == lightId }.quantity, 0.001)

        // TEST 8: Mark Support Stand as Packed. Confirm only its status changes.
        repository.togglePackingStatus(standId, true)
        val pizzaItemsAfterPack = repository.getItemsForCounter(pizzaVanId).first()
        assertTrue(pizzaItemsAfterPack.first { it.itemId == standId }.isPacked)
        assertFalse(pizzaItemsAfterPack.first { it.itemId == lightId }.isPacked)

        // TEST 9: Create Sushi Van. Add Sushi Fridge and Sauce Bowl.
        val sushiVanId = repository.insertCounter(
            name = "Sushi Van",
            description = "Japanese Counter",
            imageUri = ""
        )
        val sushiFridgeId = repository.insertItem(
            counterId = sushiVanId,
            name = "Sushi Fridge",
            quantity = 1.0,
            unit = "Pcs",
            notes = "Cold station",
            imageUri = ""
        )
        val sauceBowlId = repository.insertItem(
            counterId = sushiVanId,
            name = "Sauce Bowl",
            quantity = 20.0,
            unit = "Pcs",
            notes = "Black dipping bowls",
            imageUri = ""
        )

        // TEST 10: Open Pizza Van and confirm Sushi Van items do NOT appear.
        val pizzaItemsCheck = repository.getItemsForCounter(pizzaVanId).first()
        assertFalse(pizzaItemsCheck.any { it.itemId == sushiFridgeId })
        assertFalse(pizzaItemsCheck.any { it.itemId == sauceBowlId })
        assertEquals(2, pizzaItemsCheck.size)

        // TEST 11: Open Sushi Van and confirm Pizza Van items do NOT appear.
        val sushiItemsCheck = repository.getItemsForCounter(sushiVanId).first()
        assertFalse(sushiItemsCheck.any { it.itemId == lightId })
        assertFalse(sushiItemsCheck.any { it.itemId == standId })
        assertEquals(2, sushiItemsCheck.size)

        // TEST 12: Packing reset for a new event
        repository.resetPackingStatusForCounter(pizzaVanId)
        val pizzaItemsAfterReset = repository.getItemsForCounter(pizzaVanId).first()
        assertFalse(pizzaItemsAfterReset.first { it.itemId == standId }.isPacked)

        // Delete item verification
        repository.deleteItem(standId)
        val pizzaItemsAfterDelete = repository.getItemsForCounter(pizzaVanId).first()
        assertEquals(1, pizzaItemsAfterDelete.size)
        Unit
    }

    @Test
    fun testTeamInvitationAndProfile(): Unit = runBlocking {
        // Create Team
        val profile = repository.createTeam("Royal Caterers", "Rajesh")
        assertNotNull(profile)
        assertEquals("Royal Caterers", profile.teamName)
        assertEquals(6, profile.inviteCode.length)
        assertTrue(profile.isAdmin)

        // Verify team profile is loaded
        val currentProfile = repository.teamProfile.first()
        assertNotNull(currentProfile)
        assertEquals("Royal Caterers", currentProfile?.teamName)

        // Leave team
        repository.leaveOrResetTeam()
        val emptyProfile = repository.teamProfile.first()
        assertEquals(null, emptyProfile)
        Unit
    }
}
