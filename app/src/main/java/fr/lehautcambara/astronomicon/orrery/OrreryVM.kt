package fr.lehautcambara.astronomicon.orrery

import androidx.lifecycle.ViewModel
import fr.lehautcambara.astronomicon.angle360to180
import fr.lehautcambara.astronomicon.astrology.aspects.aspects
import fr.lehautcambara.astronomicon.astrology.localSiderealTimeHourMinute
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.ClockControlEvent
import fr.lehautcambara.astronomicon.kbus.events.DateInputEvent
import fr.lehautcambara.astronomicon.kbus.events.DateSelectedEvent
import fr.lehautcambara.astronomicon.kbus.events.LocationEvent
import fr.lehautcambara.astronomicon.kbus.events.LongitudeScrollEvent
import fr.lehautcambara.astronomicon.kbus.events.PlanetClickEvent
import fr.lehautcambara.astronomicon.kbus.events.RadialScrollEvent
import fr.lehautcambara.astronomicon.kbus.events.TimeTickEvent
import fr.lehautcambara.astronomicon.kbus.events.ZDTEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToLong

class OrreryVM : ViewModel() {

    init {
        Kbus.register(this)
        val time = ZonedDateTime.of(2016,11, 2, 21, 17, 30,0,ZoneId.of("GMT"))
        val st = time.localSiderealTimeHourMinute(6.9)
    }

    private var zonedDateTime: ZonedDateTime = ZonedDateTime.now()
    private var _uiState = MutableStateFlow(OrreryUIState(zonedDateTime))
    val uiState: StateFlow<OrreryUIState> = _uiState.asStateFlow()
    private val displayModes: Array<DisplayMode> = DisplayMode.entries.toTypedArray()


    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: ZDTEvent) {
        if (!zonedDateTime.isEqual(event.zdt)) {
            zonedDateTime = event.zdt
            _uiState.update { uistate ->
                uistate.copy(zonedDateTime = zonedDateTime)
            }
            _uiState.update { uistate ->
                uistate.copy(aspects = aspects(event.zdt))
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: PlanetClickEvent) {
        val index = (displayModes.indexOf(uiState.value.displayMode) + 1) % displayModes.size
        _uiState.update { uistate ->
            uistate.copy(displayMode = displayModes[index])
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: DateInputEvent) {
        _uiState.update { uistate ->
            uistate.copy(showDateInput = event.show)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: DateSelectedEvent) {
        event.zdt?.let { zdt ->
            Kbus.post(ZDTEvent(zdt))
            Kbus.post(DateInputEvent(false))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: TimeTickEvent) {
        if (uiState.value.updateTime) Kbus.post(ZDTEvent(ZonedDateTime.now()))
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: ClockControlEvent) {
        _uiState.update { uistate ->
            uistate.copy(updateTime = event.running)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: LocationEvent) {
        _uiState.update { uiState ->
            uiState.copy(latitude = event.latitude?:uiState.latitude, longitude = event.longitude?:uiState.longitude)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: RadialScrollEvent) {
        Kbus.post(ClockControlEvent(false))
        val scrollAmount = uiState.value.displayMode.scale(event.radialScroll()).roundToLong()
        Kbus.post(ZDTEvent(zonedDateTime.plusMinutes(scrollAmount)))
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: LongitudeScrollEvent) {
            val scrollAmount = -uiState.value.displayMode.scale(event.radialScroll()).roundToLong()
            val nl = angle360to180((uiState.value.longitude + scrollAmount/2))
            _uiState.update { uiState: OrreryUIState ->
                uiState.copy( longitude = nl )
            }
    }
}

