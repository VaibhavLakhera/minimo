package com.minimo.launcher.utils

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.net.Uri
import android.os.Build
import android.os.Process
import android.os.UserManager
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.Settings
import android.widget.Toast
import androidx.core.net.toUri
import com.minimo.launcher.R
import timber.log.Timber

fun Context.launchApp(packageName: String, className: String, userHandleHashCode: Int) {
    val userManager = getSystemService(Context.USER_SERVICE) as UserManager
    val userHandle = userManager.userProfiles.find { it.hashCode() == userHandleHashCode }

    if (packageName.isBlank() || className.isBlank() || userHandle == null) return

    val launcher = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    val component = ComponentName(packageName, className)

    try {
        launcher.startMainActivity(component, userHandle, null, null)
    } catch (exception: SecurityException) {
        Timber.e(exception)
        try {
            launcher.startMainActivity(component, Process.myUserHandle(), null, null)
        } catch (exception: Exception) {
            Timber.e(exception)
        }
    } catch (exception: Exception) {
        Timber.e(exception)
    }
}

fun Context.uninstallApp(packageName: String) {
    try {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = "package:$packageName".toUri()
        startActivity(intent)
    } catch (exception: Exception) {
        Timber.e(exception)
    }
}

fun Context.launchAppInfo(packageName: String) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    } catch (exception: Exception) {
        Timber.e(exception)
    }
}

fun Context.openHomeSettings() {
    try {
        startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
    } catch (exception: Exception) {
        Timber.e(exception)
    }
}

fun Context.openPlayStorePage(id: String = packageName) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = "https://play.google.com/store/apps/details?id=${id}".toUri()
        }
        startActivity(intent)
    } catch (exception: Exception) {
        Timber.e(exception)
    }
}

fun Context.openSeniorLauncherPlayStorePage() {
    openPlayStorePage(id = "com.eldo.launcher")
}

fun Context.sendFeedback() {
    try {
        val recipient = "vaibhav.lakhera.dev@gmail.com"
        val subject = "Minimo Launcher Feedback"
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        } else {
            Timber.e("No email app found to handle the intent.")
        }
    } catch (e: Exception) {
        Timber.e("Error sending email: ${e.message}")
    }
}

fun Context.lockScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        lockScreenWithAccessibility()
    } else {
        lockScreenWithReceiver()
    }
}

private fun Context.lockScreenWithAccessibility() {
    if (isAccessibilityEnabled()) {
        MinimoAccessibilityService.lockScreen()
    }
}

private fun Context.lockScreenWithReceiver() {
    val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val componentName = ComponentName(this, ScreenOffAdminReceiver::class.java)

    if (devicePolicyManager.isAdminActive(componentName)) {
        devicePolicyManager.lockNow()
    }
}

fun Context.hasLockScreenPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        isAccessibilityEnabled()
    } else {
        isAdminActive()
    }
}

private fun Context.isAccessibilityEnabled(): Boolean {
    var enabled = 0
    try {
        enabled = Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
    } catch (e: Settings.SettingNotFoundException) {
        Timber.e(e)
    }
    if (enabled == 1) {
        val name = ComponentName(applicationContext, MinimoAccessibilityService::class.java)
        val services = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return services?.contains(name.flattenToString()) ?: false
    }
    return false
}

private fun Context.isAdminActive(): Boolean {
    val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val componentName = ComponentName(this, ScreenOffAdminReceiver::class.java)
    return devicePolicyManager.isAdminActive(componentName)
}

fun Context.requestLockScreenPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        launchAccessibilitySettings(getString(R.string.enable_accessibility_permission_for_minimo))
    } else {
        requestEnableAdmin()
    }
}

private fun Context.launchAccessibilitySettings(toastMessage: String) {
    Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    })
}

fun Context.requestEnableAdmin() {
    val componentName = ComponentName(this, ScreenOffAdminReceiver::class.java)

    Toast.makeText(this, getString(R.string.enable_device_admin_first), Toast.LENGTH_SHORT).show()
    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
    intent.putExtra(
        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
        getString(R.string.you_need_to_enable_admin_access_to_lock_the_screen)
    )
    startActivity(intent)
}

fun Context.removeLockScreenPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        // Ignore
    } else {
        removeDeviceAdmin()
    }
}

private fun Context.removeDeviceAdmin() {
    try {
        val devicePolicyManager =
            getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(this, ScreenOffAdminReceiver::class.java)
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.removeActiveAdmin(componentName)
        }
    } catch (exception: Exception) {
        Timber.e(exception)
    }
}

fun Context.showNotificationDrawer() {
    try {
        Class.forName("android.app.StatusBarManager")
            .getMethod("expandNotificationsPanel")
            .apply { isAccessible = true }
            .invoke(getSystemService("statusbar"))
    } catch (exception: Exception) {
        Timber.e(exception)
    }
}

fun Context.openDefaultClockApp() {
    try {
        val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
        startActivity(intent)
    } catch (exception: Exception) {
        Timber.e(exception)
    }
}

fun Context.openDefaultCalendarApp() {
    if (!openDefaultCalendarAppOption1()) {
        openDefaultCalendarOption2()
    }
}

private fun Context.openDefaultCalendarAppOption1(): Boolean {
    try {
        val builder = CalendarContract.CONTENT_URI.buildUpon()
        builder.appendPath("time")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = builder.build()
        startActivity(intent)
        return true
    } catch (exception: Exception) {
        Timber.e(exception)
        return false
    }
}

private fun Context.openDefaultCalendarOption2() {
    try {
        val intent = Intent(
            Intent.makeMainSelectorActivity(
                Intent.ACTION_MAIN,
                Intent.CATEGORY_APP_CALENDAR
            )
        )
        startActivity(intent)
    } catch (exception: Exception) {
        Timber.e(exception)
    }
}