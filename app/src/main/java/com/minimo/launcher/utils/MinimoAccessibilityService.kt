package com.minimo.launcher.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent

class MinimoAccessibilityService : AccessibilityService() {
    companion object {
        private var INSTANCE: MinimoAccessibilityService? = null

        fun lockScreen() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                INSTANCE?.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onServiceConnected() {
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = 0
            packageNames = emptyArray()
        }
        INSTANCE = this
    }

    override fun onDestroy() {
        INSTANCE = null
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Ignore
    }

    override fun onInterrupt() {
        // Ignore
    }
}