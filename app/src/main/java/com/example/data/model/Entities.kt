package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "counters")
data class CounterEntity(
    @PrimaryKey
    val counterId: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val imageUri: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = CounterEntity::class,
            parentColumns = ["counterId"],
            childColumns = ["counterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["counterId"])]
)
data class ItemEntity(
    @PrimaryKey
    val itemId: String = UUID.randomUUID().toString(),
    val counterId: String,
    val name: String,
    val quantity: Double,
    val unit: String = "Pcs",
    val notes: String = "",
    val isPacked: Boolean = false,
    val section: String = "",
    val imageUri: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "team_profiles")
data class TeamProfileEntity(
    @PrimaryKey
    val teamId: String = UUID.randomUUID().toString(),
    val teamName: String,
    val inviteCode: String,
    val memberName: String,
    val isAdmin: Boolean = true,
    val joinedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "team_members")
data class TeamMemberEntity(
    @PrimaryKey
    val memberId: String = UUID.randomUUID().toString(),
    val teamId: String,
    val displayName: String,
    val isRevoked: Boolean = false,
    val joinedAt: Long = System.currentTimeMillis()
)

data class CounterWithStats(
    val counter: CounterEntity,
    val totalItems: Int,
    val totalQuantity: Double,
    val packedItems: Int
)
