package fr.lehautcambara.astronomicon

import android.app.Application
import fr.lehautcambara.astronomicon.kbus.modules.BusLogger
import fr.lehautcambara.astronomicon.kbus.modules.BusModule
import fr.lehautcambara.astronomicon.kbus.modules.ClockModule
import fr.lehautcambara.astronomicon.kbus.modules.DelayModule
import fr.lehautcambara.astronomicon.kbus.modules.FindNodeModule

class AstronomiconApplication : Application() {
    val modules: MutableSet<BusModule> = mutableSetOf()

    override fun onCreate() {
        super.onCreate()
        modules.add(BusLogger())
        modules.add(ClockModule(this))
        modules.add(DelayModule())
        modules.add(FindNodeModule())

    }
}