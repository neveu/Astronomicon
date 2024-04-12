package fr.lehautcambara.astronomicon.kbus

import org.greenrobot.eventbus.EventBus

class Kbus {
    companion object {
        fun post(busEvent: BusEvent?) {
            busEvent?.let{EventBus.getDefault().postSticky(it)}
        }
        fun <T> get(eventType: Class<T>) {
            EventBus.getDefault().getStickyEvent(eventType)
        }

        fun register(subscriber: Any) {
            EventBus.getDefault().register(subscriber)
        }

        fun unregister(subscriber: Any) {
            EventBus.getDefault().unregister(subscriber)
        }
    }
}

open class BusEvent() {
    val timestamp = System.currentTimeMillis()
}