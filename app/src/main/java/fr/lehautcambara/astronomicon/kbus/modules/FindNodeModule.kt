/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.kbus.modules

import fr.lehautcambara.astronomicon.astrology.ephemerides
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.ClockControlEvent
import fr.lehautcambara.astronomicon.kbus.events.FindNodeEvent
import fr.lehautcambara.astronomicon.kbus.events.ZDTEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.time.ZonedDateTime

class FindNodeModule : BusModule() {
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEvent(event: FindNodeEvent) {
        Kbus.post(ClockControlEvent(false))
        var zdt: ZonedDateTime
        ephemerides()[event.body]?.let { ephemeris ->
            var zdt = ephemeris.nextNode(event.zdt){ zdt ->
                Kbus.post(ZDTEvent(zdt))
            }
        }
    }
}
