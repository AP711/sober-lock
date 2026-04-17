package com.soberlock

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.soberlock.databinding.ActivityMainBinding
import com.soberlock.utils.AdMobManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var adMobManager: AdMobManager
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val deniedPermissions = permissions.filter { !it.value }.keys
        if (deniedPermissions.isNotEmpty()) {
            val permanentlyDenied = deniedPermissions.any { permission ->
                !shouldShowRequestPermissionRationale(permission)
            }
            if (permanentlyDenied) {
                showPermissionSettingsDialog()
            } else {
                showPermissionExplanation(deniedPermissions.toList())
            }
        }
    }
    
    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (!Settings.canDrawOverlays(this)) {
            showOverlayPermissionExplanation()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize AdMob
        adMobManager = AdMobManager(this)
        adMobManager.initialize()
        
        setupNavigation()
        checkPermissions()
    }
    
    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_contacts,
                R.id.navigation_stats,
                R.id.navigation_settings
            )
        )
        
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }
    
    private fun checkPermissions() {
        val requiredPermissions = mutableListOf<String>()
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.CALL_PHONE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.READ_CONTACTS)
        }
        
        if (requiredPermissions.isNotEmpty()) {
            showPermissionExplanation(requiredPermissions)
        } else {
            checkOverlayPermission()
        }
    }
    
    private fun showPermissionExplanation(permissions: List<String>) {
        val message = buildString {
            append("Sober Lock needs these permissions for core functionality:\n\n")
            permissions.forEach { permission ->
                when (permission) {
                    Manifest.permission.READ_PHONE_STATE -> append("• Phone State: Detect call events\n")
                    Manifest.permission.CALL_PHONE -> append("• Call Phone: Block outgoing calls to selected contacts\n")
                    Manifest.permission.READ_CONTACTS -> append("• Contacts: Choose which contacts to protect\n")
                }
            }
            append("\nYour privacy is important - we never share your data.")
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Permissions Required")
            .setMessage(message)
            .setPositiveButton("Grant Permissions") { _, _ ->
                if (permissions.isNotEmpty()) {
                    permissionLauncher.launch(permissions.toTypedArray())
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                showPermissionRequiredSnackbar()
            }
            .setCancelable(false)
            .show()
    }
    
    private fun showPermissionSettingsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permissions Required")
            .setMessage("Please grant the required permissions in Settings to use Sober Lock.")
            .setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }
    
    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            showOverlayPermissionExplanation()
        }
    }
    
    private fun showOverlayPermissionExplanation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Overlay Permission Required")
            .setMessage("Sober Lock needs permission to display over other apps to show the challenge screen when you try to contact protected contacts.")
            .setPositiveButton("Grant Permission") { _, _ ->
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                overlayPermissionLauncher.launch(intent)
            }
            .setNegativeButton("Skip") { _, _ -> }
            .show()
    }
    
    private fun showPermissionRequiredSnackbar() {
        Snackbar.make(
            binding.root,
            "Permissions are required for Sober Lock to function properly",
            Snackbar.LENGTH_LONG
        ).setAction("Grant") {
            checkPermissions()
        }.show()
    }
    
    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}