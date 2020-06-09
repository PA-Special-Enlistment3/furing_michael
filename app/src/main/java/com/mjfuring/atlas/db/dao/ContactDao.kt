package com.mjfuring.atlas.db.dao

import androidx.room.*
import com.mjfuring.atlas.db.model.Contact

@Dao
interface ContactDao {

    @Query("SELECT * FROM contact")
    suspend fun all(): List<Contact>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(model: Contact)

    @Delete
    suspend fun delete(model: Contact)

    @Query("UPDATE contact SET name=:name, number=:number WHERE id = :id")
    suspend fun update(id: Int, name: String, number: String)

}
