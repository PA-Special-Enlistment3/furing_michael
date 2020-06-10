package com.mjfuring.atlas.db.dao

import androidx.room.*
import com.mjfuring.atlas.common.IncidentStatus.COMPLETED
import com.mjfuring.atlas.common.IncidentStatus.INVALID
import com.mjfuring.atlas.common.IncidentStatus.RESPONDING
import com.mjfuring.atlas.db.model.Incident


@Dao
interface IncidentDao {


    @Query("SELECT * FROM incident WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Incident

    @Query("SELECT * FROM incident WHERE ref = :ref LIMIT 1")
    suspend fun getByRef(ref: Long): Incident?

    @Query("SELECT * FROM incident where status>=:status order by dateCompleted desc")
    suspend fun listCompleted(status: Int = COMPLETED): List<Incident>

    @Query("SELECT * FROM incident where status<=:status")
    suspend fun listInComplete(status: Int = RESPONDING): List<Incident>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(model: Incident): Long

    @Delete
    suspend fun delete(model: Incident)

    @Update
    suspend fun update(model: Incident)


    @Query("UPDATE incident SET status=:status, dateResponded=:date WHERE id = :id")
    suspend fun setResponded(id: Long, date: Long, status: Int = RESPONDING)

    @Query("UPDATE incident SET status=:status, dateCompleted=:date WHERE id = :id")
    suspend fun setCompleted(id: Long, date: Long, status: Int = COMPLETED)

    @Query("UPDATE incident SET status=:status, dateCompleted=:date WHERE id = :id")
    suspend fun setInvalid(id: Long, date: Long, status: Int = INVALID)

    suspend fun setResponded(id: Long): Incident {
        val request = getById(id)
        if (request.status < RESPONDING){
            setResponded(id, System.currentTimeMillis())
        }
        return getById(id)
    }

    suspend fun setCompleted(id: Long): Incident {
        val request = getById(id)
        if (request.status < COMPLETED){
            setCompleted(id, System.currentTimeMillis())
        }
        return getById(id)
    }

    suspend fun setInvalid(id: Long): Incident {
        val request = getById(id)
        if (request.status < INVALID){
            setInvalid(id, System.currentTimeMillis())
        }
        return getById(id)
    }
}
