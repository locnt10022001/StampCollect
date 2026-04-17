package com.stampcollect.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stamps",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["collectionId"])]
)
data class StampEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val collectionId: Int,
    val imagePath: String,
    val name: String = "",
    val description: String = "",
    val category: String = "All",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val rotation: Float = 0f,
    val isFavorite: Boolean = false,
    val condition: String = "",       // "Mint", "Fine", "Good", "Poor"
    val journal: String = "",         // Personal story/notes
    val country: String = "",         // Country of origin
    val timestamp: Long = System.currentTimeMillis()
)
