package pony.tothemoon.battletimer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pony.tothemoon.battletimer.model.TimerInfo

class TimerListViewModel : ViewModel() {
  var battleTimer by mutableStateOf(
    listOf(
      TimerInfo(title = "Battle Timer", time = 25 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "Battle Timer", time = 30 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "Battle Timer", time = 35 * TimerInfo.MINUTE_UNIT)
    ).random()
  )
    private set

  private val _timerListFlow = MutableStateFlow(emptyList<TimerInfo>())
  val timerListFlow: StateFlow<List<TimerInfo>> = _timerListFlow

  init {
    viewModelScope.launch {
      while (battleTimer.time >= 0) {
        delay(1 * TimerInfo.SECONDS_UNIT)

        battleTimer = battleTimer.copy(time = battleTimer.time - 1 * TimerInfo.SECONDS_UNIT)
      }
    }
    _timerListFlow.value = listOf(
      TimerInfo(title = "Timer1", time = 5 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "Timer2", time = 10 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "Timer3", time = 15 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "Timer4", time = 20 * TimerInfo.MINUTE_UNIT)
    )
  }
}