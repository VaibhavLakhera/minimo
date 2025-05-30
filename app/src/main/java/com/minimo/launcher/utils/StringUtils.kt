package com.minimo.launcher.utils

import android.content.Context
import com.minimo.launcher.R
import com.minimo.launcher.ui.theme.ThemeMode

object StringUtils {
    fun themeModeText(context: Context, mode: ThemeMode?): String {
        return when (mode) {
            ThemeMode.System -> context.getString(R.string.system)
            ThemeMode.Dark -> context.getString(R.string.dark)
            ThemeMode.Light -> context.getString(R.string.light)
            else -> ""
        }
    }

    fun homeAppsAlignmentText(context: Context, alignment: HomeAppsAlignment?): String {
        return when (alignment) {
            HomeAppsAlignment.Start -> context.getString(R.string.left)
            HomeAppsAlignment.Center -> context.getString(R.string.center)
            HomeAppsAlignment.End -> context.getString(R.string.right)
            else -> ""
        }
    }

    fun homeClockAlignmentText(context: Context, alignment: HomeClockAlignment?): String {
        return when (alignment) {
            HomeClockAlignment.Start -> context.getString(R.string.left)
            HomeClockAlignment.Center -> context.getString(R.string.center)
            HomeClockAlignment.End -> context.getString(R.string.right)
            else -> ""
        }
    }

    fun homeClockModeText(context: Context, mode: HomeClockMode?): String {
        return when (mode) {
            HomeClockMode.Full -> context.getString(R.string.full)
            HomeClockMode.TimeOnly -> context.getString(R.string.time_only)
            HomeClockMode.DateOnly -> context.getString(R.string.date_only)
            else -> ""
        }
    }
}