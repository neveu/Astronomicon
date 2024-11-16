/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.kbus.modules

import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.DelayEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DelayModule: BusModule() {
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEvent(event: DelayEvent) {
        runBlocking {
            launch {
                delay(event.delayTimeInMillis)
                Kbus.post(event.delayedEvent)
            }
        }
    }
}