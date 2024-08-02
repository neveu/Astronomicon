package fr.lehautcambara.astronomicon

import android.app.Application
import fr.lehautcambara.astronomicon.kbus.modules.AspectModule
import fr.lehautcambara.astronomicon.kbus.modules.BusLogger
import fr.lehautcambara.astronomicon.kbus.modules.BusModule

class AstronomiconApplication : Application() {
    val modules: MutableSet<BusModule> = mutableSetOf()
    init {
        modules.add(AspectModule()) // don't actually need to keep pointers to them, because the bus already does. Occasionally comes in handy for debugging
        modules.add(BusLogger())
    }
}