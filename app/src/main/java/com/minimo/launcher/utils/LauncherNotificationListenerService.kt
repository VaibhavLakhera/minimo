package com.minimo.launcher.utils

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LauncherNotificationListenerService : NotificationListenerService() {
    @Inject
    lateinit var notificationDotsNotifier: NotificationDotsNotifier

    override fun onListenerConnected() {
        super.onListenerConnected()
        updateNotificationDots()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        notificationDotsNotifier.updateNotificationDots(emptyList())
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        updateNotificationDots()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        updateNotificationDots()
    }

    private fun updateNotificationDots() {
        try {
            val notifications = activeNotifications ?: return
            val notificationDots = mutableListOf<NotificationDot>()

            for (notification in notifications) {
                val packageName = notification.packageName
                val userHandle = notification.user.hashCode()
                if (packageName != null && notification.isClearable && !notification.isOngoing) {
                    notificationDots.add(
                        NotificationDot(
                            packageName = packageName,
                            userHandle = userHandle
                        )
                    )
                }
            }

            notificationDotsNotifier.updateNotificationDots(notificationDots)
        } catch (exception: Exception) {
            Timber.e(exception)
            notificationDotsNotifier.updateNotificationDots(emptyList())
        }
    }
}