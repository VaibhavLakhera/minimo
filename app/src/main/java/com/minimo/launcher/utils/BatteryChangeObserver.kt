package com.minimo.launcher.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class BatteryChangeObserver(
    private val context: Context,
    private val onBatteryChanged: (Int) -> Unit
) : DefaultLifecycleObserver {

    private var receiver: BroadcastReceiver? = null

    override fun onResume(owner: LifecycleOwner) {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                intent?.let {
                    val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    if (level >= 0 && scale > 0) {
                        val percent = (level * 100) / scale
                        onBatteryChanged(percent)
                    }
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)
    }

    override fun onPause(owner: LifecycleOwner) {
        receiver?.let { context.unregisterReceiver(it) }
        receiver = null
    }
}
