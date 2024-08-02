package fr.lehautcambara.astronomicon.kbus.modules

import fr.lehautcambara.astronomicon.astrology.Aspects.Aspect
import fr.lehautcambara.astronomicon.astrology.Aspects.aspectEphemerisPairs
import fr.lehautcambara.astronomicon.astrology.Aspects.ephemerisPairToAspect
import fr.lehautcambara.astronomicon.astrology.AstrologicalPoints.Companion.geocentricPlanets
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.AspectsEvent
import fr.lehautcambara.astronomicon.kbus.events.ZDTEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.time.ZonedDateTime

class AspectModule : BusModule() {
    private fun aspects(zdt: ZonedDateTime ): List<Aspect> {
        return aspectEphemerisPairs(geocentricPlanets)
            .map{pair: Pair<Ephemeris, Ephemeris> ->
                ephemerisPairToAspect(pair.first, pair.second, zdt)
            }
            .filterNotNull()
    }
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEvent(event: ZDTEvent) {
        Kbus.post(AspectsEvent(aspects(event.zdt)))
    }


}