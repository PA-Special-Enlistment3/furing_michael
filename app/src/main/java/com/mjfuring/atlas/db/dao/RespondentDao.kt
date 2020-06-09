package com.mjfuring.atlas.db.dao

import androidx.room.*
import com.mjfuring.atlas.db.model.Contact
import com.mjfuring.atlas.db.model.Respondent

@Dao
interface RespondentDao {

    @Query("SELECT * FROM respondent WHERE ref = :ref AND number = :no LIMIT 1")
    suspend fun findDuplicate(ref: Int, no: String): Respondent?

    @Query("SELECT * FROM respondent  WHERE ref = :ref order by dateReceived")
    suspend fun getByRequest(ref: Int): List<Respondent>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(model: Respondent)

}
