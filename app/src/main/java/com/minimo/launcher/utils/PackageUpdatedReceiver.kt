package com.minimo.launcher.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.minimo.launcher.data.usecase.AddUpdateAppsUseCase
import com.minimo.launcher.data.usecase.RemoveAppsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@AndroidEntryPoint
class PackageUpdatedReceiver : BroadcastReceiver() {
    @Inject
    lateinit var addUpdateAppsUseCase: AddUpdateAppsUseCase

    @Inject
    lateinit var removeAppsUseCase: RemoveAppsUseCase

    fun getFilter(): IntentFilter {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED)
        filter.addDataScheme("package")
        return filter
    }

    override fun onReceive(context: Context, intent: Intent) = goAsync {
        val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
        val packageName = intent.data?.schemeSpecificPart

        if (packageName.isNullOrBlank()) return@goAsync

        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                addUpdateAppsUseCase.invoke(packageName)
            }

            Intent.ACTION_PACKAGE_REMOVED -> {
                if (!isReplacing) {
                    removeAppsUseCase.invoke(packageName)
                }
            }

            Intent.ACTION_PACKAGE_CHANGED -> {
                addUpdateAppsUseCase.invoke(packageName)
            }
        }
    }
}

private fun BroadcastReceiver.goAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) {
    val pendingResult = goAsync()
    @OptIn(DelicateCoroutinesApi::class)
    GlobalScope.launch(context) {
        try {
            block()
        } finally {
            pendingResult.finish()
        }
    }
}