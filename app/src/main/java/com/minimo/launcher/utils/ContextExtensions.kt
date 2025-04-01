package com.minimo.launcher.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.net.toUri
import timber.log.Timber

fun Context.launchApp(packageName: String): Boolean {
    try {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return true
        }
    } catch (exception: Exception) {
        Timber.e(exception)
    }
    return false
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