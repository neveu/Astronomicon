package fr.lehautcambara.astronomicon.orrery

import androidx.lifecycle.ViewModel
import angled
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.RadialScrollEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.time.ZonedDateTime
import java.util.GregorianCalendar
import kotlin.math.abs
import kotlin.math.roundToLong

class OrreryVM( ) : ViewModel() {

    init {
        Kbus.register(this)
    }
    private var zonedDateTime: ZonedDateTime = ZonedDateTime.now()
    private var _uiState = MutableStateFlow(OrreryUIState(zonedDateTime))
    val uiState: StateFlow<OrreryUIState> = _uiState.asStateFlow()

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: RadialScrollEvent) {
        val scrollAmount = (event.radialScroll()/200F).roundToLong()
        zonedDateTime = zonedDateTime.plusHours(scrollAmount)
        _uiState.update { uistate ->
            uistate.copy(zonedDateTime = zonedDateTime)
        }
    }

    fun fromTo(from: Ephemeris?, to: Ephemeris?): Coords? {
        val currentDate = _uiState.value.julianCentury
        if (from == null) return to?.eclipticCoords(currentDate)
        return if (to == null) -from.eclipticCoords(currentDate)
        else to.eclipticCoords(currentDate) - from.eclipticCoords(currentDate)
    }

    fun angle(from: Ephemeris?, to: Ephemeris?): Double {
        fromTo(from, to)?.apply {
            return angled(x, y)
        }
        return 0.0
    }

    fun aspectAngle(center: Ephemeris?, from: Ephemeris?, to: Ephemeris?): Double {
        return abs(angle(center, from) - angle(center, to))
    }
    fun aspect(angleFromTo: Double, aspectAngle: Double, error: Double): Boolean {
        return abs(aspectAngle - angleFromTo) < error
    }

}

private operator fun Coords.unaryMinus(): Coords? {
    return Coords(-x, -y, -z)
}

private operator fun Coords.minus(eclipticCoords: Coords): Coords {
    return Coords(
        x - eclipticCoords.x,
        y - eclipticCoords.y,
        z - eclipticCoords.z
    )
}
