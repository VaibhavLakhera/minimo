package com.minimo.launcher.utils

import android.os.Build

object AndroidUtils {
    fun isDynamicThemeSupported() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}