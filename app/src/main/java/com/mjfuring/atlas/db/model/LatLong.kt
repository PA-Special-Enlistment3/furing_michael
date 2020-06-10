package com.mjfuring.atlas.db.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LatLong(
    val lat: Double = 0.0,
    val lon: Double = 0.0
) : Parcelable