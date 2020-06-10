package com.mjfuring.atlas.db.dao

import androidx.room.*
import com.mjfuring.atlas.common.IncidentStatus
import com.mjfuring.atlas.db.model.Contact
import com.mjfuring.atlas.db.model.Respondent

@Dao
interface RespondentDao {

    @Query("SELECT * FROM respondent WHERE ref = :ref AND number = :no LIMIT 1")
    suspend fun findDuplicate(ref: Long, no: String): Respondent?

    @Query("SELECT * FROM respondent WHERE ref = :id order by dateReceived")
    suspend fun listByRequest(id: Long): List<Respondent>

    @Query("SELECT id FROM respondent  WHERE ref = :ref AND number = :no LIMIT 1" )
    suspend fun getByNumber(ref: Long, no: String): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(model: Respondent): Long

    @Query("UPDATE respondent SET status=:status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: Int)

}
