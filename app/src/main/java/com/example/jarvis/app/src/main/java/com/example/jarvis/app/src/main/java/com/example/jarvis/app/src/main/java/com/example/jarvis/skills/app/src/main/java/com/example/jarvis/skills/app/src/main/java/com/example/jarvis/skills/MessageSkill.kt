package com.example.jarvis.skills

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.telephony.SmsManager
import androidx.core.content.ContextCompat

/**
 * Handles: "message john saying I'm on my way"
 * Splits on " saying " to separate contact name from message body.
 */
class MessageSkill : Skill {

    override fun matches(command: String): Boolean {
        return command.startsWith("message ") && command.contains(" saying ")
    }

    override fun execute(context: Context, command: String): String {
        val body = command.removePrefix("message ").trim()
        val parts = body.split(" saying ", limit = 2)
        if (parts.size != 2) {
            return "Say it like: message John saying your text, sir."
        }
        val name = parts[0].trim()
        val text = parts[1].trim()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return "I don't have permission to send messages yet, sir."
        }

        val number = findContactNumber(context, name)
            ?: return "I couldn't find $name in your contacts, sir."

        val smsManager = context.getSystemService(SmsManager::class.java)
        smsManager.sendTextMessage(number, null, text, null, null)
        return "Message sent to $name, sir."
    }

    private fun findContactNumber(context: Context, name: String): String? {
        val resolver = context.contentResolver
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$name%")

        resolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                return cursor.getString(numberIndex)
            }
        }
        return null
    }
}
