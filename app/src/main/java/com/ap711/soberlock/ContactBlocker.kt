package com.ap711.soberlock

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson

class ContactBlocker(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "sober_lock_encrypted",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val gson = Gson()

    fun isSessionActive(): Boolean {
        val isActive = prefs.getBoolean("session_active", false)
        val endTime = prefs.getLong("session_end", 0)
        return isActive && System.currentTimeMillis() < endTime
    }

    fun activateSession(durationMinutes: Int) {
        if (durationMinutes <= 0 || durationMinutes > 1440) return
        val endTime = System.currentTimeMillis() + (durationMinutes * 60 * 1000L)
        prefs.edit()
            .putBoolean("session_active", true)
            .putLong("session_end", endTime)
            .apply()
    }

    fun deactivateSession() {
        prefs.edit()
            .putBoolean("session_active", false)
            .remove("session_end")
            .apply()
    }

    fun isContactBlocked(phoneNumber: String): Boolean {
        if (phoneNumber.isBlank()) return false
        if (!isSessionActive()) return false
        val blockedContacts = getBlockedContacts()
        val normalizedNumber = normalizePhoneNumber(phoneNumber)
        return blockedContacts.any { normalizePhoneNumber(it) == normalizedNumber }
    }

    private fun normalizePhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace(Regex("[^0-9+]"), "")
            .removePrefix("+1")
            .takeLast(10)
    }

    fun getBlockedContacts(): Set<String> {
        return prefs.getStringSet("blocked_contacts", emptySet()) ?: emptySet()
    }

    fun addBlockedContact(phoneNumber: String) {
        if (phoneNumber.isBlank()) return
        val normalizedNumber = normalizePhoneNumber(phoneNumber)
        if (normalizedNumber.length < 10) return
        val current = getBlockedContacts().toMutableSet()
        current.add(normalizedNumber)
        if (current.size <= 1000) {
            prefs.edit().putStringSet("blocked_contacts", current).apply()
        }
    }

    fun removeBlockedContact(phoneNumber: String) {
        if (phoneNumber.isBlank()) return
        val normalizedNumber = normalizePhoneNumber(phoneNumber)
        val current = getBlockedContacts().toMutableSet()
        current.remove(normalizedNumber)
        prefs.edit().putStringSet("blocked_contacts", current).apply()
    }

    fun incrementSaveCount() {
        val current = prefs.getInt("save_count", 0)
        if (current < Int.MAX_VALUE - 1) {
            prefs.edit().putInt("save_count", current + 1).apply()
        }
    }

    fun getSaveCount(): Int = prefs.getInt("save_count", 0)
}