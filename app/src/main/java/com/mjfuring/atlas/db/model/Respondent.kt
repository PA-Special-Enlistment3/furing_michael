package com.mjfuring.atlas.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Respondent(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var ref: Int = 0,
    var number: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var dateReceived:Long = 0
)