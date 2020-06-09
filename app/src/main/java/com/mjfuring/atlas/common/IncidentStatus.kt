package com.mjfuring.atlas.common

import com.mjfuring.atlas.common.IncidentStatus.COMPLETED
import com.mjfuring.atlas.common.IncidentStatus.DUPLICATE
import com.mjfuring.atlas.common.IncidentStatus.INVALID
import com.mjfuring.atlas.common.IncidentStatus.PENDING
import com.mjfuring.atlas.common.IncidentStatus.RESPONDING


object IncidentStatus {
    const val PENDING: Int = 1
    const val RESPONDING: Int = 2
    const val COMPLETED: Int = 3
    const val INVALID: Int = 4
    const val DUPLICATE: Int = 5

}

fun Int.toRequestStatus(): String{
    return when(this){
        PENDING -> "Needs Assistance"
        RESPONDING -> "Acknowledge"
        COMPLETED -> "Completed"
        INVALID -> "Invalid"
        DUPLICATE -> "Duplicate"
        else -> ""
    }
}
