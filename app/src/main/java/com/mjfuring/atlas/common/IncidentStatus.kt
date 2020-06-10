package com.mjfuring.atlas.common

import com.mjfuring.atlas.common.IncidentStatus.COMPLETED
import com.mjfuring.atlas.common.IncidentStatus.DUPLICATE
import com.mjfuring.atlas.common.IncidentStatus.INVALID
import com.mjfuring.atlas.common.IncidentStatus.PENDING
import com.mjfuring.atlas.common.IncidentStatus.RESPONDING
import com.mjfuring.atlas.common.RespondentStatus.ARRIVED
import com.mjfuring.atlas.common.RespondentStatus.SENT


object IncidentStatus {
    const val PENDING: Int = 1
    const val RESPONDING: Int = 2
    const val COMPLETED: Int = 3
    const val INVALID: Int = 4
    const val DUPLICATE: Int = 5

}

fun Int.toRequestStatus(): String{
    return when(this){
        PENDING -> "Pending"
        RESPONDING -> "Responding"
        COMPLETED -> "Completed"
        INVALID -> "Invalid"
        DUPLICATE -> "Duplicate"
        else -> ""
    }
}

object RespondentStatus {
    const val SENT: Int = 1
    const val RESPONDING: Int = 2
    const val ARRIVED: Int = 3
    const val FAILED: Int = 4
}

fun Int.toRespondentStatus(): String{
    return when(this){
        SENT -> "Incident Reported"
        RespondentStatus.RESPONDING -> "Responding"
        ARRIVED -> "Arrived"
        RespondentStatus.FAILED -> "Failed"
        else -> ""
    }
}