/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.kbus.modules

import android.util.Log
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.ClockControlEvent
import fr.lehautcambara.astronomicon.kbus.events.FindNodeEvent
import fr.lehautcambara.astronomicon.kbus.events.ZDTEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.time.format.DateTimeFormatter

class FindNodeModule : BusModule() {
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEvent(event: FindNodeEvent) {
        Kbus.post(ClockControlEvent(false))
        Ephemeris.ephemerides()[event.body]?.let { ephemeris ->
            ephemeris.nextNode(event.zdt){ zdt ->
                Log.d("FindNodeModule zdt changed: ", zdt.format(DateTimeFormatter.ISO_DATE_TIME))
                Kbus.post(ZDTEvent(zdt))
            }
        }
    }
}
