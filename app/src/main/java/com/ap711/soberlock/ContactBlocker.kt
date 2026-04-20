package com.ap711.soberlock

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import java.security.GeneralSecurityException

class ContactBlocker(private val context: Context) {
    private val prefs: SharedPreferences by lazy {
        try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                "sober_lock_encrypted",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: GeneralSecurityException) {
            context.getSharedPreferences("sober_lock", Context.MODE_PRIVATE)
        }
    }
    private val gson = Gson()

    fun isSessionActive(): Boolean {
        val isActive = prefs.getBoolean("session_active", false)
        val endTime = prefs.getLong("session_end", 0)
        
        if (isActive && System.currentTimeMillis() > endTime) {
            deactivateSession()
            return false
        }
        
        return isActive
    }

    fun activateSession(durationMinutes: Int) {
        if (durationMinutes <= 0) return
        
        val endTime = System.currentTimeMillis() + (durationMinutes * 60 * 1000L)
        prefs.edit()
            .putBoolean("session_active", true)
            .putLong("session_end", endTime)
            .apply()
    }

    fun deactivateSession() {
        prefs.edit()
            .putBoolean("session_active", false)
            .putLong("session_end", 0)
            .apply()
    }

    fun isContactBlocked(phoneNumber: String): Boolean {
        if (phoneNumber.isBlank()) return false
        if (!isSessionActive()) return false
        val blockedContacts = getBlockedContacts()
        return blockedContacts.contains(phoneNumber.trim())
    }

    fun getBlockedContacts(): Set<String> {
        return prefs.getStringSet("blocked_contacts", emptySet())?.toSet() ?: emptySet()
    }

    fun addBlockedContact(phoneNumber: String) {
        if (phoneNumber.isBlank()) return
        
        val current = getBlockedContacts().toMutableSet()
        current.add(phoneNumber.trim())
        prefs.edit().putStringSet("blocked_contacts", current).apply()
    }

    fun removeBlockedContact(phoneNumber: String) {
        if (phoneNumber.isBlank()) return
        
        val current = getBlockedContacts().toMutableSet()
        current.remove(phoneNumber.trim())
        prefs.edit().putStringSet("blocked_contacts", current).apply()
    }

    fun incrementSaveCount() {
        val current = prefs.getInt("save_count", 0)
        if (current < Int.MAX_VALUE) {
            prefs.edit().putInt("save_count", current + 1).apply()
        }
    }

    fun getSaveCount(): Int = prefs.getInt("save_count", 0)
}