package pony.tothemoon.battletimer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pony.tothemoon.battletimer.model.TimerInfo

class BattleTimerViewModel: ViewModel() {
  var battleTimer by mutableStateOf(
    listOf(
      TimerInfo(title = "Battle Timer", time = 25 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "Battle Timer", time = 30 * TimerInfo.MINUTE_UNIT),
      TimerInfo(title = "Battle Timer", time = 35 * TimerInfo.MINUTE_UNIT)
    ).random()
  )
    private set

  init {
    viewModelScope.launch {
      while (battleTimer.time >= 0) {
        delay(1 * TimerInfo.SECONDS_UNIT)

        battleTimer = battleTimer.copy(time = battleTimer.time - 1 * TimerInfo.SECONDS_UNIT)
      }
    }
  }
}