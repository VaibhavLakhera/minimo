package com.minimo.launcher.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.minimo.launcher.ui.main.MainActivity
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

fun Context.chooseDefaultLauncher() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    intent.putExtra(
        Intent.EXTRA_CHOOSER_TARGETS,
        arrayOf(ComponentName(this, MainActivity::class.java))
    )
    startActivity(Intent.createChooser(intent, "Choose default launcher"))
}