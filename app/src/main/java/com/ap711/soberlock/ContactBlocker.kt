package com.ap711.soberlock

import android.content.Context
import android.content.SharedPreferences

class ContactBlocker(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sober_lock_prefs", Context.MODE_PRIVATE)

    fun isSessionActive(): Boolean = prefs.getBoolean("session_active", false)

    fun activateSession(durationMinutes: Int) {
        val endTime = System.currentTimeMillis() + (durationMinutes * 60 * 1000L)
        prefs.edit()
            .putBoolean("session_active", true)
            .putLong("session_end", endTime)
            .apply()
    }

    fun deactivateSession() {
        prefs.edit().putBoolean("session_active", false).apply()
    }

    fun isContactBlocked(phoneNumber: String): Boolean {
        if (!isSessionActive()) return false
        return getBlockedContacts().contains(phoneNumber)
    }

    fun getBlockedContacts(): Set<String> {
        return prefs.getStringSet("blocked_contacts", emptySet()) ?: emptySet()
    }

    fun addBlockedContact(phoneNumber: String) {
        val current = getBlockedContacts().toMutableSet()
        current.add(phoneNumber)
        prefs.edit().putStringSet("blocked_contacts", current).apply()
    }

    fun removeBlockedContact(phoneNumber: String) {
        val current = getBlockedContacts().toMutableSet()
        current.remove(phoneNumber)
        prefs.edit().putStringSet("blocked_contacts", current).apply()
    }

    fun incrementSaveCount() {
        val current = prefs.getInt("save_count", 0)
        prefs.edit().putInt("save_count", current + 1).apply()
    }

    fun getSaveCount(): Int = prefs.getInt("save_count", 0)
}
