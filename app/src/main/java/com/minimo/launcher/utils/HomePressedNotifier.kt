package com.minimo.launcher.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomePressedNotifier @Inject constructor() {
    private val _homePressedEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val homePressedEvent = _homePressedEvent.asSharedFlow()

    fun notifyHomePressed() {
        _homePressedEvent.tryEmit(Unit)
    }
}