package fr.lehautcambara.astronomicon.kbus.modules

import android.util.Log
import fr.lehautcambara.astronomicon.kbus.events.LogEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BusLogger : BusModule() {
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: LogEvent) {
        Log.d(">>>>>>>>", event.msg)
    }

}