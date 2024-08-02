package fr.lehautcambara.astronomicon.kbus.modules

import fr.lehautcambara.astronomicon.kbus.Kbus

open class BusModule {
    init {
        Kbus.register(this)
    }
}
