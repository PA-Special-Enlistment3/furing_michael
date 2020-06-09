package com.mjfuring.atlas.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mjfuring.atlas.db.dao.ContactDao
import com.mjfuring.atlas.db.dao.RequestDao
import com.mjfuring.atlas.db.dao.RespondentDao
import com.mjfuring.atlas.db.model.Contact
import com.mjfuring.atlas.db.model.Incident
import com.mjfuring.atlas.db.model.Respondent


@Database(entities = [Contact::class, Incident::class, Respondent::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactDAO(): ContactDao
    abstract fun requestDAO(): RequestDao
    abstract fun respondentDAO(): RespondentDao

}
