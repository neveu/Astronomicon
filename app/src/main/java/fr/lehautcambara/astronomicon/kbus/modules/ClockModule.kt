/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.kbus.modules

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.ClockControlEvent
import fr.lehautcambara.astronomicon.kbus.events.TimeTickEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ClockModule(context: Context) : BusModule() {
    var receiver: BroadcastReceiver
    init {
        receiver = object:  BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action?.compareTo(Intent.ACTION_TIME_TICK)==0) {

                    Kbus.post(TimeTickEvent())
                }
            }
        }
        context.registerReceiver(this.receiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEvent(event: ClockControlEvent) {

    }

}