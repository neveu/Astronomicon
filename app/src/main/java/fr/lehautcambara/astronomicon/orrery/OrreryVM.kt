package fr.lehautcambara.astronomicon.orrery

import androidx.lifecycle.ViewModel
import fr.lehautcambara.astronomicon.astrology.aspects.aspects
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.PlanetClickEvent
import fr.lehautcambara.astronomicon.kbus.events.RadialScrollEvent
import fr.lehautcambara.astronomicon.kbus.events.ZDTEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.time.ZonedDateTime
import kotlin.math.roundToLong

class OrreryVM : ViewModel() {

    init {
        Kbus.register(this)
    }
    private var zonedDateTime: ZonedDateTime = ZonedDateTime.now()
    private var _uiState = MutableStateFlow(OrreryUIState(zonedDateTime))
    val uiState: StateFlow<OrreryUIState> = _uiState.asStateFlow()
    private val displayModes: Array<DisplayMode> = DisplayMode.values()

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: RadialScrollEvent) {
        val scrollAmount = uiState.value.displayMode.scale(event.radialScroll()).roundToLong()
        Kbus.post(ZDTEvent(zonedDateTime.plusMinutes(scrollAmount)))
    }

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
        val newDisplayMode = displayModes[index]
        _uiState.update { uistate ->
            uistate.copy(displayMode = newDisplayMode)
        }
    }

}

