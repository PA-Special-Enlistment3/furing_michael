package com.mjfuring.atlas.setup

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjfuring.atlas.common.PREF_MAP_DOWNLOAD
import com.mjfuring.atlas.common.getContactNumbers
import com.mjfuring.atlas.common.getPhoneContacts
import com.mjfuring.atlas.db.dao.ContactDao
import com.mjfuring.atlas.db.model.Contact
import com.mjfuring.base.event.EventLiveData
import com.mjfuring.base.event.posting
import com.mjfuring.base.event.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class VmSetup(
    private val pref: SharedPreferences,
    private val contactDao: ContactDao
): ViewModel() {

    val fragmentEvent = EventLiveData()
    val contactEvent = EventLiveData()
    val importEvent = EventLiveData()
    val homeEvent = EventLiveData()

    fun gotoFragment(pos: Int){
        fragmentEvent.success(pos)
    }

    fun isMapDownloaded(): Boolean{
        return pref.getBoolean(PREF_MAP_DOWNLOAD, false)
    }

    fun mapDownloaded(){
        pref.edit().apply{
            putBoolean(PREF_MAP_DOWNLOAD, true)
            apply()
        }
        homeEvent.success()
    }

    fun getContacts(context: Context){

        viewModelScope.launch(Dispatchers.IO) {

            contactEvent.posting()

            val contactsListAsync = async { getPhoneContacts(context) }
            val contactNumbersAsync = async { getContactNumbers(context) }

            val contacts = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()

            contacts.forEach {
                contactNumbers[it.id]?.let { numbers ->
                    it.number = numbers[0]
                }
            }

            contactEvent.success(contacts)

        }

    }

    fun importContact(contacts: ArrayList<Contact>){
        viewModelScope.launch(Dispatchers.IO) {
            importEvent.posting()
            contacts.forEach {
                contactDao.add(it)
            }
            importEvent.success()
        }
    }


}