package fr.lehautcambara.astronomicon

import android.app.Application
import fr.lehautcambara.astronomicon.kbus.modules.BusLogger
import fr.lehautcambara.astronomicon.kbus.modules.BusModule

class AstronomiconApplication : Application() {
    val modules: MutableSet<BusModule> = mutableSetOf()
    init {
        modules.add(BusLogger())
    }
}