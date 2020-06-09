package com.mjfuring.atlas.common

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.mjfuring.atlas.db.model.Contact
import java.util.*

fun getPhoneContacts(context: Context): ArrayList<Contact> {
    val contactsList = ArrayList<Contact>()
    val contactsCursor = context.contentResolver?.query(
        ContactsContract.Contacts.CONTENT_URI,
        null,
        null,
        null,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
    if (contactsCursor != null && contactsCursor.count > 0) {
        val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
        val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
        while (contactsCursor.moveToNext()) {
            val id = contactsCursor.getString(idIndex)
            val name = contactsCursor.getString(nameIndex)
            if (name != null) {
                contactsList.add(Contact(id, name))
            }
        }
        contactsCursor.close()
    }
    return contactsList
}

fun getContactNumbers(context: Context): HashMap<String, ArrayList<String>> {
    val contactsNumberMap = HashMap<String, ArrayList<String>>()
    val phoneCursor: Cursor? = context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        null
    )
    if (phoneCursor != null && phoneCursor.count > 0) {
        val contactIdIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
        val numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        while (phoneCursor.moveToNext()) {
            val contactId = phoneCursor.getString(contactIdIndex)
            val number: String = phoneCursor.getString(numberIndex)
            //check if the map contains key or not, if not then create a new array list with number
            if (contactsNumberMap.containsKey(contactId)) {
                contactsNumberMap[contactId]?.add(number)
            } else {
                contactsNumberMap[contactId] = arrayListOf(number)
            }
        }
        //contact contains all the number of a particular contact
        phoneCursor.close()
    }
    return contactsNumberMap
}