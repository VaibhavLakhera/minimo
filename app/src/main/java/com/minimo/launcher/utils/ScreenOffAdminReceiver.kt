package com.minimo.launcher.utils

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.minimo.launcher.R

class ScreenOffAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Toast.makeText(context, context.getString(R.string.admin_enabled), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Toast.makeText(context, context.getString(R.string.admin_disabled), Toast.LENGTH_SHORT)
            .show()
    }
}
