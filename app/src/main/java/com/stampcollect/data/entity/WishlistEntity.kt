package com.stampcollect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val imagePath: String? = null,
    val estimatedPrice: String = "",
    val isFound: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
