package com.mjfuring.atlas.db.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Incident(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var ref: Int = 0,
    var title: String = "",
    var number: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var dateCreated:Long = 0,
    var dateCompleted:Long = 0,
    var status: Int = 0
) : Parcelable