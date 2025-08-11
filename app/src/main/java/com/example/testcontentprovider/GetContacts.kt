package com.example.testcontentprovider

import android.content.Context
import android.provider.ContactsContract

object GetContacts {
    fun fetchContacts(context: Context): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver = context.contentResolver
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
        )
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        cursor?.use { c ->
            while (c.moveToNext()) {
                val id = c.getLong(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name =
                    c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                var phone: String? = null
                contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(id.toString()),
                    null
                )?.use { phoneCursor ->
                    if (phoneCursor.moveToFirst()) {
                        phone = phoneCursor.getString(
                            phoneCursor.getColumnIndexOrThrow(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                    }
                }

                var email: String? = null
                contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    arrayOf(id.toString()),
                    null
                )?.use { emailCursor ->
                    if (emailCursor.moveToFirst()) {
                        email = emailCursor.getString(
                            emailCursor.getColumnIndexOrThrow(
                                ContactsContract.CommonDataKinds.Email.ADDRESS
                            )
                        )
                    }
                }
                contacts.add(Contact(id, name, phone, email))
            }
        }
        return contacts
    }
}