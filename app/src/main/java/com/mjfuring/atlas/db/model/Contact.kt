package com.mjfuring.atlas.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey
    var id: String,
    var name: String,
    var number: String = "",
    var selected: Boolean = false
)