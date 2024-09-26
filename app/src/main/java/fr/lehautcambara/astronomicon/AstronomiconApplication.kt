package fr.lehautcambara.astronomicon

import android.app.Application
import fr.lehautcambara.astronomicon.kbus.modules.BusLogger
import fr.lehautcambara.astronomicon.kbus.modules.BusModule
import fr.lehautcambara.astronomicon.kbus.modules.ClockModule

class AstronomiconApplication : Application() {
    val modules: MutableSet<BusModule> = mutableSetOf()

    override fun onCreate() {
        super.onCreate()
        modules.add(BusLogger())
        modules.add(ClockModule(this))

    }
}