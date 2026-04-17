package com.stampcollect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val backgroundType: Int = 0,
    val orderIndex: Int = 0, // 0: Parchment, 1: Grid, 2: Cork, 3: Velvet
    val timestamp: Long = System.currentTimeMillis()
)
