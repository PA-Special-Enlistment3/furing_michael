package com.mjfuring.atlas.db.model

data class JsonSms(
    val cmd: Int = 0,
    val ref: Int = 0,
    val lat: Double = 0.0,
    val lon: Double = 0.0
)