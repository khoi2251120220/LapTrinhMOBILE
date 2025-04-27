package com.example.restaurantmanage.data.firebase

import com.example.restaurantmanage.data.local.entity.TableEntity

data class FirestoreTable(
    val id: String = "",
    val name: String = "",
    val capacity: Int = 0,
    val status: String = "AVAILABLE", // AVAILABLE, RESERVED, OCCUPIED
    val image: String = "" // Path to table image
) {
    // Convert Firestore table to Room entity
    fun toTableEntity(localId: Int = 0): TableEntity {
        return TableEntity(
            id = localId,
            name = name,
            capacity = capacity,
            status = status,
            image = image
        )
    }
    
    companion object {
        // Convert Room entity to Firestore table
        fun fromTableEntity(tableEntity: TableEntity): FirestoreTable {
            return FirestoreTable(
                id = tableEntity.id.toString(),
                name = tableEntity.name,
                capacity = tableEntity.capacity,
                status = tableEntity.status,
                image = tableEntity.image
            )
        }
    }
} 