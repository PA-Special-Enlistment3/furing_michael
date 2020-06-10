package com.mjfuring.atlas.db.model

data class JsonSms(
    val cmd: Int = 0,
    val ref: Long = 0,
    val nat: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0
)